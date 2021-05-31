package cn.how2j.trend.web;

import cn.how2j.trend.pojo.AnnualProfit;
import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.pojo.Profit;
import cn.how2j.trend.pojo.Trade;
import cn.how2j.trend.service.BackTestService;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**返回的数据是放在一个 Map 里的，
 * 而目前的key是 indexDatas
 *
 * 增加对ma参数的接收
 */
@RestController
public class BackTestController {
    @Autowired BackTestService backTestService;

    //获取指定的开始时间，结束时间，以及这段区间的indexDates
    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String,Object> backTest(
            @PathVariable("code") String code
            ,@PathVariable("ma") int ma
            ,@PathVariable("buyThreshold") float buyThreshold
            ,@PathVariable("sellThreshold") float sellThreshold
            ,@PathVariable("serviceCharge") float serviceCharge
            ,@PathVariable("startDate") String strStartDate
            ,@PathVariable("endDate") String strEndDate
    ) throws Exception {
        //拿到指定代码的指数数据
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);
        //获取整个数据的开始日期和结束日期
        String indexStartDate = allIndexDatas.get(0).getDate();
        String indexEndDate = allIndexDatas.get(allIndexDatas.size()-1).getDate();
        //根据开始日期和结束日期获取对应的日期范围的数据
        allIndexDatas = filterByDateRange(allIndexDatas,strStartDate, strEndDate);

        //设置ma天数、买入率、卖出率，服务费率
        //int ma = 20;
        float sellRate = sellThreshold;
        float buyRate = buyThreshold;
        //float serviceCharge = 0f;
        Map<String,?> simulateResult= backTestService.simulate(ma,sellRate, buyRate,serviceCharge, allIndexDatas);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");
        List<Trade> trades = (List<Trade>) simulateResult.get("trades");
        //获取年份跨度
        float years = backTestService.getYear(allIndexDatas);
        //计算指数的收益和趋势投资的收益，以及对应的年化收益率。
        //一直持有
        float indexIncomeTotal = (allIndexDatas.get(allIndexDatas.size()-1).getClosePoint() - allIndexDatas.get(0).getClosePoint()) / allIndexDatas.get(0).getClosePoint();
        //趋势投资
        float indexIncomeAnnual = (float) Math.pow(1+indexIncomeTotal, 1/years) - 1;
        float trendIncomeTotal = (profits.get(profits.size()-1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;

        //计算统计信息
        int winCount = (Integer) simulateResult.get("winCount");
        int lossCount = (Integer) simulateResult.get("lossCount");
        float avgWinRate = (Float) simulateResult.get("avgWinRate");
        float avgLossRate = (Float) simulateResult.get("avgLossRate");

        List<AnnualProfit> annualProfits = (List<AnnualProfit>) simulateResult.get("annualProfits");
        //记录目标区间indexData数据，开始日期，结束日期
        Map<String,Object> result = new HashMap<>();
        result.put("indexDatas", allIndexDatas);
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        result.put("profits", profits);
        result.put("trades", trades);
        result.put("years", years);
        result.put("indexIncomeTotal", indexIncomeTotal);
        result.put("indexIncomeAnnual", indexIncomeAnnual);
        result.put("trendIncomeTotal", trendIncomeTotal);
        result.put("trendIncomeAnnual", trendIncomeAnnual);

        result.put("winCount", winCount);
        result.put("lossCount", lossCount);
        result.put("avgWinRate", avgWinRate);
        result.put("avgLossRate", avgLossRate);

        result.put("annualProfits", annualProfits);

        return result;
    }

    //取目标时间区间的indexData
    private List<IndexData> filterByDateRange(List<IndexData> allIndexDatas, String strStartDate, String strEndDate) {
        //保证开始日期早于结束日期
        if(StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate) )
            return allIndexDatas;

        List<IndexData> result = new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);

        for (IndexData indexData : allIndexDatas) {
            Date date =DateUtil.parse(indexData.getDate());
            //取目标时间区间的数据
            if(
                    date.getTime()>=startDate.getTime() &&
                            date.getTime()<=endDate.getTime()
            ) {
                result.add(indexData);
            }
        }
        return result;
    }

}