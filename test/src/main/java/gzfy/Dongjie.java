package gzfy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.json.JSONObject;


public class Dongjie {

	public static void main(String[] args) {
//		System.out.println("\n冻结金额："+specialSort());
		List<Map<String,Object>> persons = new ArrayList<Map<String,Object>>();
		String person = "{执行人信息=[{诉讼地位=申请人, 姓名或名称=徐思兵, 证件号码=512222196710011176}, {诉讼地位=被执行人, 姓名或名称=仁怀市五马镇白石沟煤矿, 证件号码=55660102-0}], 立案日期=2016-10-13, 多个被执行人=[{涉行政案件=1, 涉行政案件数=1件, 涉执行案件数=4件, 被执行人序号=2, 涉执行案件=4, 房地产=[{房产证=遵字第631427815号, 房产位置=贵州省遵义市仁怀市五马镇龙里村, 估值=2068730.0, 房产面积=430}], 曾被纳入失信被执行人名单=否, 车辆=[{品牌=依维柯, 估值=119900.0, 车牌号码=贵CD6216, 车辆类型=H32}, {品牌=东风多利卡D8, 估值=165000.0, 车牌号码=贵CN5236, 车辆类型=Q11}], 涉民事案件=5, 曾被纳入重大税收违法当事人=否, 涉民事案件数=5件, 银行存款=[{账户状态=正常, 金额=40118.0, 开户银行=贵州银行, 账户号码=02530011000002320001}, {账户状态=正常, 金额=832562.0, 开户银行=遵义市商业银行, 账户号码=6214600180007095453}, {账户状态=正常, 金额=300637.0, 开户银行=中国工商银行, 账户号码=6222023803013297860}], 被执行人名称=仁怀市五马镇白石沟煤矿, 曾被纳入企业异常经营名单=否}], 案号=(2016)黔03执329号, 案由=建设工程合同纠纷, 案件来源=申请执行人申请, 执行依据文书号=(2016)黔03民初491号, 执行依据类型=具有执行内容民事案件生效判决书、裁定书、调解书、支付令, 做出执行依据的单位=遵义市中级人民法院, 执行标的金额=5750000.00, 每个案号估值合计=3526947.0}";
		
		JSONObject json = JSONObject.fromObject(person);
		persons.add(json);
		new_zhixingfanan(persons);
	}
	/**
	 * 冻结金额取优算法
	 * 
	 * */
	
	public static Double specialSort(){
		List<Double> arry = new LinkedList<Double>();
		
		arry.add(4.2);
		arry.add(24.2);
		arry.add(34.2);
		arry.add(14.2);
		arry.add(7.2);
		arry.add(23.2);
		arry.add(5.2);
//		arry.add(34.2);
		Double biaodi = 90.9;
		Double delta = -1.0;
		while(arry.size()>0&&biaodi>0){
			//取大于标的金额的最小值
			arry.sort(new Comparator<Double>() {
				@Override
				public int compare(Double o1, Double o2) {
					// TODO Auto-generated method stub
					if(o1>o2){
						return 1;
					}else{
						return -1;
					}
				}
			});
			
			System.out.println("\n删除前,标的金额"+biaodi);
			for(Double e:arry){
				System.out.print(e+"\t");
			}
			System.out.print("\n");
			for(Double e:arry){
				if(e>biaodi){
					delta = e;
					break;
				}
			}
			if(delta<0){
				delta = arry.get(arry.size()-1);
			}
			
			arry.remove(delta);
			if(biaodi<delta){
				delta = biaodi;
				break;
			}else{
				biaodi -= delta;
			}
			System.out.println("冻结金额："+delta+",标的金额"+biaodi);
			System.out.println("删除后：");
			for(Double e:arry){
				System.out.print(e+"\t");
			}
			delta = -1.0;
		}
		return delta;
	}
	
	public static void new_zhixingfanan(List<Map<String, Object>> persons){
		boolean flag = true;// 作用是标志当前变化的标的金额是否小于0，如果小于，则置为false
		Double biaodi = 0D;
		Double biaodi_change = 0D;
		boolean flag_bank = false,flag_house = false,flag_car = false;//判断是否由冻结的各类财产
		List<Map<String, Object>> allBeizhixingren = new ArrayList<Map<String, Object>>();
		
		for(int i=0;i<persons.size();i++){
			Map<String, Object> dongjie = new HashMap<String, Object>();// 存储每一个被执行人的所有财产，包括银行/现金/车辆等等
			Map<String, Object> descriptions = new HashMap<String, Object>();// 存储当前被执行人的所有财产和被执行人序号
			Map<String, Object> person = persons.remove(i);
			
			String xuhao = person.get("被执行人序号").toString();
			descriptions.put("被执行人序号", xuhao);
			descriptions.put("被执行人名称", person.get("被执行人名称"));
			
			// ****************************处理银行存款*********************
						if (flag == true && null != person.get("银行存款")) {
							Double delta = -1.0;

							List<Map<String, Object>> money = (List) person.get("银行存款");//获取多条银行存款
							// 按照金额的大小给每个银行账户排序，金额从低到高
							money = money.stream().sorted((a, b) -> Double.valueOf(b.get("金额").toString())
									.compareTo(Double.valueOf(a.get("金额").toString()))).collect(Collectors.toList());

							List<Map<String, Object>> dataList = new ArrayList<>();//存储资产细节描述的
							double dongjieSum = 0;//存储该被执行人的总的冻结银行存款
							
							int flag_index= money.size()-1;
							if(money.size()>0){
								flag_bank = true;
							}
							//去除不必冻结的金额
							for (int j = 0; j < money.size(); j++) {
								Map<String, Object> m = money.get(j);
								double sum = Double.valueOf(m.get("金额").toString());
								if (sum / biaodi > 0.1 && sum > 500) {
									money.remove(j);
								}
							}
							for (int j = 0; j < money.size(); j++) {
								Map<String, Object> m = money.get(j);
								double sum = Double.valueOf(m.get("金额").toString());
								if(sum > biaodi_change){
									delta = sum;
									flag_index = j;
									break;
								}
							}
							if(delta<0&&money.size()>0){
								delta = Double.valueOf(money.get(money.size()-1).get("金额").toString());
							}
							if(biaodi_change<delta){
								flag = false;
								delta = biaodi_change;
								break;
							}else{
								biaodi_change -= delta;
							}
							dongjieSum += delta;
							Map<String, Object> m = money.get(flag_index);
							String accout = "账户号码：【" + m.get("账户号码") + "】，所属银行：【" + m.get("开户银行") + "】，冻结金额：【"
									+ delta + "元】";
							m.put("描述", accout);
							dataList.add(m);
							
							money.remove(flag_index);
							
							delta = -1.0;
							dongjie.put("银行数据", dataList);
							dongjie.put("银行标题", "、" + "冻结被执行人" + person.get("被执行人名称") + "如下银行存款共计"
									+ dongjieSum + "元，在执行款到账后三十日内， 并及时划拨、发放给申请执行人。");
							person.put("银行存款", money);
						}
						// *************************************处理现金
						if (flag == true && null != person.get("现金")) {
							Double delta = -1.0;
							List<Map<String, Object>> money = (List) person.get("现金");
							// 按照金额的高低排序金额从低到高
							money = money.stream().sorted((a, b) -> Double.valueOf(b.get("数额").toString())
									.compareTo(Double.valueOf(a.get("数额").toString()))).collect(Collectors.toList());

							List<Map<String, Object>> dataList = new ArrayList<>();
							double dongjieSum = 0;
							int flag_index= money.size()-1;
							for (int j = 0; j < money.size(); j++) {
								Map<String, Object> m = money.get(j);
								double sum = Double.valueOf(m.get("数额").toString());
								if(sum > biaodi_change){
									delta = sum;
									flag_index = j;
									break;
								}
							}
							if(delta<0&&money.size()>0){
								delta = Double.valueOf(money.get(money.size()-1).get("数额").toString());
							}
							if(biaodi_change<delta){
								flag = false;
								delta = biaodi_change;
								break;
							}else{
								biaodi_change -= delta;
							}
							dongjieSum += delta;
							Map<String, Object> m = money.get(flag_index);
							String accout = "币种：【" + m.get("币种") + "】，估值：【" + delta + "元】";
							m.put("描述", accout);
							dataList.add(m);
							money.remove(flag_index);
							delta = -1.0;
							dongjie.put("现金数据", dataList);
							dongjie.put("现金标题","、" + "扣押搜查到的被执行人" + person.get("被执行人名称") + "现金" + dongjieSum
									+ "，及时发放申请人。");
							person.put("现金", money);
						}
						// **************************************处理车辆
						if (flag == true && null != person.get("车辆")) {
							Double delta = -1.0;
							List<Map<String, Object>> money = (List) person.get("车辆");
							// 按照金额的高低排序,金额从低到高
							money = money.stream().sorted((a, b) -> Double.valueOf(b.get("估值").toString())
									.compareTo(Double.valueOf(a.get("估值").toString()))).collect(Collectors.toList());
							List<Map<String, Object>> dataList = new ArrayList<>();
							int flag_index= money.size()-1;
							for (int j = 0; j < money.size(); j++) {
								Map<String, Object> m = money.get(j);
								double sum = Double.valueOf(m.get("估值").toString());
								if(sum > biaodi_change){
									delta = sum;
									flag_index = j;
									break;
								}
							}
							if(delta<0&&money.size()>0){
								delta = Double.valueOf(money.get(money.size()-1).get("数额").toString());
							}
							if(biaodi_change<delta){
								flag = false;
								delta = biaodi_change;
								break;
							}else{
								biaodi_change -= delta;
							}
							Map<String, Object> m = money.get(flag_index);
							String accout = "车牌号码：【" + m.get("车牌号码") + "】，品牌：【" + m.get("品牌") + "】，估值：【" + delta
							+ "元】";
							m.put("描述", accout);
							dataList.add(m);
							money.remove(flag_index);
							delta = -1.0;
							dongjie.put("车辆数据", dataList);
							dongjie.put("车辆标题",
									"、" + "扣押被执行人" + person.get("被执行人名称") + "名下的如下车辆，并委托第三方机构评估、公开拍卖。");
							person.put("车辆", money);
						}
						// *************************************处理房产
						if (flag == true && null != person.get("房地产")) {
							Double delta = -1.0;
							List<Map<String, Object>> money = (List) person.get("房地产");
							// 按照金额的高低排序,金额从低到高
							money = money.stream().sorted((a, b) -> Double.valueOf(b.get("估值").toString())
									.compareTo(Double.valueOf(a.get("估值").toString()))).collect(Collectors.toList());
							List<Map<String, Object>> dataList = new ArrayList<>();
							int flag_index= money.size()-1;
							for (int j = 0; j < money.size(); j++) {
								Map<String, Object> m = money.get(j);
								double sum = Double.valueOf(m.get("估值").toString());
								if(sum > biaodi_change){
									delta = sum;
									flag_index = j;
									break;
								}
							}
							if(delta<0&&money.size()>0){
								delta = Double.valueOf(money.get(money.size()-1).get("估值").toString());
							}
							if(biaodi_change<delta){
								flag = false;
								delta = biaodi_change;
								break;
							}else{
								biaodi_change -= delta;
							}
							Map<String, Object> m = money.get(flag_index);
							String accout =  "房产证号：【" + m.get("房产证") + "】，位置：【" + m.get("房产位置") + "】，面积：【" + m.get("房产面积")
							+ "】，估值【" + delta + "元】；";
							m.put("描述", accout);
							dataList.add(m);
							money.remove(flag_index);
							delta = -1.0;
							dongjie.put("房产数据", dataList);
							dongjie.put("房产标题",
								 "、" + "查封被执行人" + person.get("被执行人名称") + "如下房产，并委托第三方机构评估、公开拍卖。");
					
						}
						// ************************************************处理股权
						if (flag == true && null != person.get("股权")) {
							Double delta = -1.0;
							List<Map<String, Object>> money = (List) person.get("股权");
							// 按照金额的高低排序,金额从低到高
							money = money.stream().sorted((a, b) -> Double.valueOf(b.get("出资额").toString())
									.compareTo(Double.valueOf(a.get("出资额").toString()))).collect(Collectors.toList());
							List<Map<String, Object>> dataList = new ArrayList<>();
							int flag_index= money.size()-1;
							for (int j = 0; j < money.size(); j++) {
								Map<String, Object> m = money.get(j);
								double sum = Double.valueOf(m.get("出资额").toString());
								if(sum > biaodi_change){
									delta = sum;
									flag_index = j;
									break;
								}
							}
							if(delta<0&&money.size()>0){
								delta = Double.valueOf(money.get(money.size()-1).get("估值").toString());
							}
							if(biaodi_change<delta){
								flag = false;
								delta = biaodi_change;
								break;
							}else{
								biaodi_change -= delta;
							}
							Map<String, Object> m = money.get(flag_index);
							String accout =  "持股公司名称：【" + m.get("持股公司名称") + "】，持股比例：【" + m.get("持股比例") + "】，出资额：【"
									+ delta + "元】；";
							m.put("描述", accout);
							dataList.add(m);
							money.remove(flag_index);
							delta = -1.0;
							dongjie.put("股权数据", dataList);
							dongjie.put("股权标题",
									"、" + "依申请人申请冻结被执行人" + person.get("被执行人名称") + "如下股权，并以评估和拍卖的方式将股权变价 。");
						}
						descriptions.put("所有财产", dongjie);
						allBeizhixingren.add(descriptions);
						
						persons.add(person);	
		}
	}
}
