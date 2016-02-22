package car.sharewhere.gagan.utills;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ameba on 22/12/15.
 */
public class Getter_setter {

    String trip_id;
    String customer_id;
    String vehicle_type;
    String mobile_number;
    String vehicle_description;
    String VehicleNo;
    String round;
    String seats_available;
    String rate_per_seat;
    String Trip_status;
    String notifi_send_to;
    String notifi_status;
    String notifi_request_id;
    String notifi_send_by;
    String vehicle_number;
    String regulardays;
    String vehicle_name;
    String Is_regular;
    String mid_point;
    String return_time;
    String return_date;
    String leaving_time;
    String leaving_date;
    String leaving_to;
    String leaving_from;
    String image;
    String customer_name;
    String Model_CustomerPhoto;
    String Model_CustomerMobileNo;
    String Model_CustomerName;
    String Model_CustomerId;
    String Model_TripId;
    String RequestAcceptModelList;
    String trip_gcm_flag;



    public String getTrip_gcm_flag() {
        return trip_gcm_flag;
    }

    public void setTrip_gcm_flag(String trip_gcm_flag) {
        this.trip_gcm_flag = trip_gcm_flag;
    }


    public String getModel_CustomerMobileNo() {
        return Model_CustomerMobileNo;
    }

    public void setModel_CustomerMobileNo(String model_CustomerMobileNo) {
        Model_CustomerMobileNo = model_CustomerMobileNo;
    }

    public String getModel_CustomerPhoto() {
        return Model_CustomerPhoto;
    }

    public void setModel_CustomerPhoto(String model_CustomerPhoto) {
        Model_CustomerPhoto = model_CustomerPhoto;
    }

    public String getModel_CustomerName() {
        return Model_CustomerName;
    }

    public void setModel_CustomerName(String model_CustomerName) {
        Model_CustomerName = model_CustomerName;
    }

    public String getModel_CustomerId() {
        return Model_CustomerId;
    }

    public void setModel_CustomerId(String model_CustomerId) {
        Model_CustomerId = model_CustomerId;
    }

    public String getModel_TripId() {
        return Model_TripId;
    }

    public void setModel_TripId(String model_TripId) {
        Model_TripId = model_TripId;
    }

    public String getVehicleNo()
    {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo)
    {
        VehicleNo = vehicleNo;
    }

    public String getRequestAcceptModelList() {
        return RequestAcceptModelList;
    }

    public void setRequestAcceptModelList(String requestAcceptModelList) {
        RequestAcceptModelList = requestAcceptModelList;
    }

    //*************************************************************************************************

    ArrayList<HashMap<String,String>> child_list;

    public ArrayList<HashMap<String, String>> getChild_list()
    {
        return child_list;
    }

    public void setChild_list(ArrayList<HashMap<String, String>> child_list)
    {
        this.child_list = child_list;
    }



    //*************************************************************************************************





    public String getNotifi_request_id() {
        return notifi_request_id;
    }

    public void setNotifi_request_id(String notifi_request_id) {
        this.notifi_request_id = notifi_request_id;
    }

    public String getNotifi_status() {
        return notifi_status;
    }

    public void setNotifi_status(String notifi_status) {
        this.notifi_status = notifi_status;
    }

    public String getNotifi_send_to() {
        return notifi_send_to;
    }

    public void setNotifi_send_to(String notifi_send_to) {
        this.notifi_send_to = notifi_send_to;
    }

    public String getNotifi_send_by() {
        return notifi_send_by;
    }

    public void setNotifi_send_by(String notifi_send_by) {
        this.notifi_send_by = notifi_send_by;
    }

    public String getTrip_status() {
        return Trip_status;
    }

    public void setTrip_status(String trip_status) {
        Trip_status = trip_status;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getRegulardays() {
        return regulardays;
    }

    public void setRegulardays(String regulardays) {
        this.regulardays = regulardays;
    }

    public String getRate_per_seat() {
        return rate_per_seat;
    }

    public void setRate_per_seat(String rate_per_seat) {
        this.rate_per_seat = rate_per_seat;
    }

    public String getSeats_available() {
        return seats_available;
    }

    public void setSeats_available(String seats_available) {
        this.seats_available = seats_available;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getVehicle_description() {
        return vehicle_description;
    }

    public void setVehicle_description(String vehicle_description) {
        this.vehicle_description = vehicle_description;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLeaving_from() {
        return leaving_from;
    }

    public void setLeaving_from(String leaving_from) {
        this.leaving_from = leaving_from;
    }

    public String getLeaving_to() {
        return leaving_to;
    }

    public void setLeaving_to(String leaving_to) {
        this.leaving_to = leaving_to;
    }

    public String getLeaving_date() {
        return leaving_date;
    }

    public void setLeaving_date(String leaving_date) {
        this.leaving_date = leaving_date;
    }

    public String getLeaving_time() {
        return leaving_time;
    }

    public void setLeaving_time(String leaving_time) {
        this.leaving_time = leaving_time;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public String getReturn_time() {
        return return_time;
    }

    public void setReturn_time(String return_time) {
        this.return_time = return_time;
    }

    public String getMid_point() {
        return mid_point;
    }

    public void setMid_point(String mid_point) {
        this.mid_point = mid_point;
    }

    public String getIs_regular() {
        return Is_regular;
    }

    public void setIs_regular(String is_regular) {
        Is_regular = is_regular;
    }
}
