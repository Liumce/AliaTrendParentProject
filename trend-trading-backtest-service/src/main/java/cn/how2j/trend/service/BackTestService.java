package cn.how2j.trend.service;

import cn.how2j.trend.client.IndexDataClient;
import cn.how2j.trend.pojo.AnnualProfit;
import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.pojo.Profit;
import cn.how2j.trend.pojo.Trade;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**用于提供所有模拟回测数据
 *
 * （1）提供指定code的指数链表indexDatas
 * （2）提供指定code的根据投资策略计算的盈利profit list
 * （3）提供指定code的交易记录
 * （4）计算指定code的数据跨度有多少年
 * （5）交易的统计信息，盈利次数，亏损次数，胜率，平均盈利，平均亏损
 */
@Service
public class BackTestService {
    @Autowired IndexDataClient indexDataClient;

    //提供指定code的指数链表indexDatas
    public List<IndexData> listIndexData(String code){
        List<IndexData> result = indexDataClient.getIndexData(code);
        Collections.reverse(result);

//      for (IndexData indexData : result) {
//          System.out.println(indexData.getDate());
//      }

        return result;
    }
    //提供指定code的根据投资策略计算的盈利profit +交易记录+ 交易的统计信息，盈利次数，亏损次数，胜率，平均盈利，平均亏损
    public Map<String,Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas)  {

        List<Profit> profits =new ArrayList<>();
        List<Trade> trades = new ArrayList<>();

        float initCash = 1000;
        float cash = initCash;
        float share = 0; //持有股票份额cash/closePoint
        float value = 0;

        //统计信息
        int winCount = 0;
        float totalWinRate = 0;
        float avgWinRate = 0;
        float totalLossRate = 0;
        int lossCount = 0;
        float avgLossRate = 0;

        //初始收盘点
        float init =0;
        if(!indexDatas.isEmpty())
            init = indexDatas.get(0).getClosePoint();

        // 遍历
        for (int i = 0; i<indexDatas.size() ; i++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            //计算当前日期i前面ma天的收盘点均值
            float avg = getMA(i,ma,indexDatas); //均线
            //计算当前日期i前面ma天的收盘点最大值
            float max = getMax(i,ma,indexDatas);
            //计算增长率：第i天的收盘点/前面ma天的收盘点均值
            float increase_rate = closePoint/avg;
            //计算下降率：第i天的收盘点/前面ma天的收盘点最大值
            float decrease_rate = closePoint/max;

            //根据策略进行操作
            if(avg!=0) {
                //buy 超过了均线
                if(increase_rate>buyRate  ) {
                    //如果之前没买入，现在买入
                    if(0 == share) {
                        share = cash / closePoint;
                        cash = 0;
                        //购买时创建一个交易对象
                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getDate());
                        trade.setBuyClosePoint(indexData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }
                //sell 低于了卖点
                else if(decrease_rate<sellRate ) {
                    //如果之前没卖，则卖出
                    if(0!= share){
                        cash = closePoint * share * (1-serviceCharge);
                        share = 0;
                        //出售时修改前面创建的交易对象
                        Trade trade =trades.get(trades.size()-1);
                        trade.setSellDate(indexData.getDate());
                        trade.setSellClosePoint(indexData.getClosePoint());
                        //对当前买入的进行操作
                        float rate = cash / initCash;
                        trade.setRate(rate);

                        if(trade.getSellClosePoint()-trade.getBuyClosePoint()>0) {
                            totalWinRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            winCount++;
                        }

                        else {
                            totalLossRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            lossCount ++;
                        }
                    }
                }
                //do nothing
                else{

                }
            }

            //将实际持股进行估价
            if(share!=0) {//持有股票
                value = closePoint * share;
            }
            else {//不持有股票
                value = cash;
            }
            //计算当前i的投资盈利率
            float rate = value/initCash;

            Profit profit = new Profit();
            profit.setDate(indexData.getDate());
            profit.setValue(rate*init);//初始收盘点*投资盈利率

            profits.add(profit);

        }

        avgWinRate = totalWinRate / winCount;
        avgLossRate = totalLossRate / lossCount;

        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);

        Map<String,Object> map = new HashMap<>();
        map.put("profits", profits);
        map.put("trades", trades);

        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);

        map.put("annualProfits", annualProfits);
        return map;
    }

    /**
     * 日期i的前面day天内，最大的收盘点
     */
    private static float getMax(int i, int day, List<IndexData> list) {
        int start = i-1-day;
        if(start<0)
            start = 0;
        int now = i-1;

        if(start<0)
            return 0;

        //max: day天内最大的收盘点
        float max = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            if(bean.getClosePoint()>max) {
                max = bean.getClosePoint();
            }
        }
        return max;
    }

    //当前日期i，前面ma天的收盘点均值
    private static float getMA(int i, int ma, List<IndexData> list) {
        int start = i-1-ma;
        int now = i-1;

        if(start<0)
            return 0;

        float sum = 0;
        float avg = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            sum += bean.getClosePoint();
        }
        avg = sum / (now - start);
        return avg;
    }

    //计算当前数据的时间范围是多少年
    public float getYear(List<IndexData> allIndexDatas) {
        float years;
        String sDateStart = allIndexDatas.get(0).getDate();
        String sDateEnd = allIndexDatas.get(allIndexDatas.size()-1).getDate();
        //开始时间//结束时间
        Date dateStart = DateUtil.parse(sDateStart);
        Date dateEnd = DateUtil.parse(sDateEnd);

        long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        years = days/365f;
        return years;
    }

    //计算完整时间范围内，每一年的指数投资收益和趋势投资收益
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        String strStartDate = indexDatas.get(0).getDate();
        String strEndDate = indexDatas.get(indexDatas.size()-1).getDate();

        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        //获取开始年份和结束年份
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);

        for (int year =startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);

            float indexIncome = getIndexIncome(year,indexDatas);
            float trendIncome = getTrendIncome(year,profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);

        }
        return result;
    }
    //计算某一年的趋势投资收益
    private float getIndexIncome(int year, List<IndexData> indexDatas) {
        IndexData first=null;
        IndexData last=null;

        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
//          Date date = DateUtil.parse(strDate);
            int currentYear = getYear(strDate);

            if(currentYear == year) {
                if(null==first)
                    first = indexData; //第一次给first赋值
                last = indexData; //每一次都进行赋值，最后存储最后一个赋值
            }
        }
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }

    //计算year年的指数收益
    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;

        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);

            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }

    //获取某个日期中的年份：eg 2019-05-21 => 2019
    private int getYear(String date) {
        String strYear= StrUtil.subBefore(date, "-", false);
        return Convert.toInt(strYear);
    }
}