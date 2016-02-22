package car.sharewhere.gagan.WebServices;

import org.json.JSONObject;

public interface Asnychronus_notifier {

	public void onResultsSucceeded_Get_Method1(JSONObject result);
	public void onResultsSucceeded_Get_Method2(JSONObject result);

	public void onResultsSucceeded_Post_Method1(JSONObject result);
	public void onResultsSucceeded_Post_Method2(JSONObject result);
	public void onResultsSucceeded_Post_Method3(JSONObject result);
}
