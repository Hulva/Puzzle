package hulva.luva.wxx.platform.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONParse {
	
	public static Map<String, String> map(String json) {
		Map<String, String> resultMap = new HashMap<String, String>();
		parseJsonToMap(resultMap, json, null);
		return resultMap;
	}

    public static void parseJsonToMap(Map<String, String> treeMap, String json, String key) {
    	if(json == null) { return; }
    	Object value = JSON.parse(json);
        if (value instanceof JSONArray) {
        	if(key == null) { key = "data"; }
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++) {
            	Object obj = array.get(i);
            	treeMap.put(key + "[" + i + "]" + "_DATA", json);
            	parseJsonToMap(treeMap, JSON.toJSONString(obj), key + "[" + i + "]");
            }
            if(key != null) { treeMap.put(key + "_DATA", json); }
        } else if (value instanceof JSONObject) {
            JSONObject object = (JSONObject) value;
            for (Map.Entry<String, Object> element : object.entrySet()) {
            	String ckey = key + "." + element.getKey();
            	if(key == null) { ckey = element.getKey(); }
            	parseJsonToMap(treeMap, JSON.toJSONString(element.getValue()), ckey);
            }
            if(key != null) { treeMap.put(key + "_DATA", json); }
        }else if(key != null){
        	treeMap.put(key, json);
        }
    }
    
    public static void main(String[] args) {
		String json = "{\"SalesOrders\":[{\"Basic\":{\"SONumber\":455505613,\"CustomerNumber\":66362087,\"CustomerPONumber\":\"\",\"Status\":\"C\",\"Type\":\"1\",\"ShipViaCode\":\"006\",\"ShipViaDescription\":\"Super EggSaver (2-5 Bus. days)\",\"ReferenceSONumber\":\"455505613\",\"SpecialComment\":\"##Vertex##\",\"IPAddress\":\"99.150.197.164\",\"RiskScore\":45,\"OriginalInvoiceNumber\":0,\"StoreSign\":\"N\",\"SplitFlag\":\"09\",\"SoReason\":\"Y\",\"InvoiceNumber\":170588978,\"SOMemo\":\"##Vertex##\",\"InvoiceRequired\":\"Y\",\"Duties\":0,\"ShoppingCartID\":\"PFXPMIDWVKQMTO0\",\"SODate\":\"/Date(1562103943000-0700)/\",\"SalesPerson\":\"EGG\",\"SalesPerson_Name\":\"New Egg\",\"Version\":0,\"EmailAddress\":\"james-is-here@sbcglobal.net\",\"LoginName\":\"james-is-here@sbcglobal.net\",\"EditDateTime\":\"/Date(1562166903000-0700)/\",\"LanguageCode\":\"en-us\",\"ShippingCountry\":\"USA\",\"CompanyCode\":1003,\"SAPImportRequired\":\"Y\",\"AddressVerifyMark\":\"Y\",\"VerifyUser\":\"Accertify\",\"VerifyUser_Name\":\"Accertify\",\"VerifyDate\":\"/Date(1562104328000-0700)/\",\"OnlineApproveDate\":\"/Date(1562103992000-0700)/\",\"DisapproveReason\":\"Y,08868D,AVS:Y,CVV2:M\",\"PartialShippmentEnabled\":\"N\",\"OnLinePrintMark\":false,\"PrintCounter\":0,\"LastEditUser\":\"Egg\",\"LastEditUser_Name\":\"New Egg\",\"LastEditDate\":\"/Date(1562166903000-0700)/\",\"OpenReason\":\"\",\"HoldMark\":false,\"HoldUser\":\"\",\"SalesPostUser\":\"EGG\",\"SalesPostUser_Name\":\"New Egg\",\"SalesPostDate\":\"/Date(1562104349000-0700)/\",\"AcctPostUser\":\"EGG\",\"AcctPostUser_Name\":\"New Egg\",\"AcctPostDate\":\"/Date(1562104349000-0700)/\",\"WarehousePostUser\":\"\",\"TaxRate\":0,\"RushOrderFee\":0,\"ShippingCharge\":3.99,\"CurrencyShippingCharge\":3.99,\"TaxAmount\":0,\"CurrencyTaxAmount\":0,\"SOAmount\":113.97,\"CurrencySOAmount\":113.97,\"CurrencyCode\":\"USD\",\"RevenueOwnerCompanyCode\":1003,\"CurrencyExchangeRate\":1,\"ReferShipOutDate\":\"/Date(1562104349000-0700)/\"},\"Shipping\":{\"AddressID\":100125247,\"CompanyName\":\"\",\"ContactWith\":\"hyunguk kim\",\"Address1\":\"6010 Reese Rd\",\"Address2\":\"Apt 203\",\"City\":\"Davie\",\"State\":\"FL\",\"Zipcode\":\"33314-1221\",\"Country\":\"USA\",\"Phone\":\"714-864-8815\",\"Fax\":\"\"},\"Billing\":{\"CompanyName\":\"\",\"ContactWith\":\"hyunguk kim\",\"Address1\":\"2448 Bulrush Cir\",\"Address2\":\"\",\"City\":\"Corona\",\"State\":\"CA\",\"Zipcode\":\"92882-7988\",\"Country\":\"USA\",\"Phone\":\"714-864-8815\"},\"Payment\":{\"CreditCard1\":\"4***********2695\",\"ExpireDate1\":\"09/23\",\"CreditCard2\":\"888-888-8888\",\"ExpireDate2\":\"****\",\"BankPhone\":\"888-888-8888\",\"CVV2\":\"****\",\"AVS\":\"Y\",\"CreditCardVerifyMark\":\"G\",\"CreditCardCharged\":\"1\",\"PayTermsCode\":\"001\",\"PtermDescription\":\"VISA\",\"Payterms\":\"\",\"VerifyStatus\":\"G\"},\"Transactions\":[{\"ItemNumber\":\"11-146-296\",\"Description\":\"CASE NZXT CA-H200B-B1 R\",\"AverageCost\":74.5,\"LastCost\":74.5,\"CurrentCost\":74.5,\"UnitPrice\":69.99,\"Quantity\":1,\"ExtendPrice\":69.99,\"ItemSource\":0,\"WarehouseNumber\":\"09\",\"ShippingCharge\":0,\"GLCode\":\"\",\"GroupID\":\"B\",\"RepairWarrantyDays\":30,\"RefundWarrantyDays\":30,\"ManufacturerWarrantyDays\":19674,\"Discount\":0,\"Indate\":\"/Date(1562103971000-0700)/\",\"ReferenceSONumber\":\"455505613\",\"AppliedShippingCharge\":0,\"OwnerCost\":0,\"DutiesRate\":0,\"CurrencyUnitPrice\":69.99,\"CurrencyExtendPrice\":69.99,\"CurrencyShippingCharge\":0,\"TotalPriceDiscount\":0,\"TotalShippingDiscount\":0,\"TaxRate\":0,\"PriceTaxAmount\":0,\"ShipTaxAmount\":0,\"CurrencyAverageCost\":74.5,\"CurrencyLastCost\":74.5,\"SOQty\":1,\"ReservedFlag1\":\"\"},{\"ItemNumber\":\"35-608-029\",\"Description\":\"CPU COOLING NOCTUA | NH-L9i R\",\"AverageCost\":35.5,\"LastCost\":35.5,\"CurrentCost\":35.5,\"UnitPrice\":39.99,\"Quantity\":1,\"ExtendPrice\":39.99,\"ItemSource\":0,\"WarehouseNumber\":\"09\",\"ShippingCharge\":3.99,\"GLCode\":\"\",\"GroupID\":\"B\",\"RepairWarrantyDays\":30,\"RefundWarrantyDays\":30,\"ManufacturerWarrantyDays\":19674,\"Discount\":0,\"Indate\":\"/Date(1562103971000-0700)/\",\"ReferenceSONumber\":\"455505613\",\"AppliedShippingCharge\":0,\"OwnerCost\":0,\"DutiesRate\":0,\"CurrencyUnitPrice\":39.99,\"CurrencyExtendPrice\":39.99,\"CurrencyShippingCharge\":3.99,\"TotalPriceDiscount\":0,\"TotalShippingDiscount\":0,\"TaxRate\":0,\"PriceTaxAmount\":0,\"ShipTaxAmount\":0,\"CurrencyAverageCost\":35.5,\"CurrencyLastCost\":35.5,\"SOQty\":1,\"ReservedFlag1\":\"\"}]}],\"TotalCount\":1,\"PageIndex\":1,\"IsEnableSOBigData\":true}";
		Map<String, String> resultMap = new HashMap<String, String>();
		parseJsonToMap(resultMap, json, null);
		resultMap.forEach((k,v)->{
			if(k.endsWith("_DATA")) {
				System.out.println(k + " : " + v);
			}
		});
	}
}
