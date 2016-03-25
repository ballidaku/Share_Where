package car.sharewhere.gagan.utills;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import car.sharewhere.gagan.sharewherecars.Ride_Details;

/**
 Created by sharan on 15/2/16. */
public class Bean_Ride_Details
{
    String CreatedOn, CustomerContactNo, CustomerId, CustomerName, CustomerPhoto, DepartureDate, DepartureTime, Description, IsActive, IsRegulerBasis;
    String LeavingDate, LeavingFrom, LeavingTo, NoOfSeats, RatePerSeat, RegulerDays, RoundTrip;
    String TripId, VehicleName, VehicleNo, VehicleType, ReturnDate, ReturnTime, TripStatus, RequestId="";

    ArrayList<HashMap<String, String>> RouteModelList = new ArrayList<>();

    public String getRequestId()
    {
        return RequestId;
    }

    public void setRequestId(String requestId)
    {
        RequestId = requestId;
    }

    public String getTripStatus()
    {
        return TripStatus;
    }

    public void setTripStatus(String tripStatus)
    {
        TripStatus = tripStatus;
    }

    ArrayList<HashMap<String, String>> RequestAcceptModelList = new ArrayList<>();

    public String getReturnDate()
    {
        return ReturnDate;
    }

    public void setReturnDate(String returnDate)
    {
        ReturnDate = returnDate;
    }

    public String getReturnTime()
    {
        return ReturnTime;
    }

    public void setReturnTime(String returnTime)
    {
        ReturnTime = returnTime;
    }

    public String getCreatedOn()
    {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn)
    {
        CreatedOn = createdOn;
    }

    public String getCustomerContactNo()
    {
        return CustomerContactNo;
    }

    public void setCustomerContactNo(String customerContactNo)
    {
        CustomerContactNo = customerContactNo;
    }

    public String getCustomerId()
    {
        return CustomerId;
    }

    public void setCustomerId(String customerId)
    {
        CustomerId = customerId;
    }

    public String getCustomerName()
    {
        return CustomerName;
    }

    public void setCustomerName(String customerName)
    {
        CustomerName = customerName;
    }

    public String getCustomerPhoto()
    {
        return CustomerPhoto;
    }

    public void setCustomerPhoto(String customerPhoto)
    {
        CustomerPhoto = customerPhoto;
    }

    public String getDepartureDate()
    {
        return DepartureDate;
    }

    public void setDepartureDate(String departureDate)
    {
        DepartureDate = departureDate;
    }

    public String getDepartureTime()
    {
        return DepartureTime;
    }

    public void setDepartureTime(String departureTime)
    {
        DepartureTime = departureTime;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public String getIsActive()
    {
        return IsActive;
    }

    public void setIsActive(String isActive)
    {
        IsActive = isActive;
    }

    public String getIsRegulerBasis()
    {
        return IsRegulerBasis;
    }

    public void setIsRegulerBasis(String isRegulerBasis)
    {
        IsRegulerBasis = isRegulerBasis;
    }

    public String getLeavingDate()
    {
        return LeavingDate;
    }

    public void setLeavingDate(String leavingDate)
    {
        LeavingDate = leavingDate;
    }

    public String getLeavingFrom()
    {
        return LeavingFrom;
    }

    public void setLeavingFrom(String leavingFrom)
    {
        LeavingFrom = leavingFrom;
    }

    public String getLeavingTo()
    {
        return LeavingTo;
    }

    public void setLeavingTo(String leavingTo)
    {
        LeavingTo = leavingTo;
    }

    public String getNoOfSeats()
    {
        return NoOfSeats;
    }

    public void setNoOfSeats(String noOfSeats)
    {
        NoOfSeats = noOfSeats;
    }

    public String getRatePerSeat()
    {
        return RatePerSeat;
    }

    public void setRatePerSeat(String ratePerSeat)
    {
        RatePerSeat = ratePerSeat;
    }

    public String getRegulerDays()
    {
        return RegulerDays;
    }

    public void setRegulerDays(String regulerDays)
    {
        RegulerDays = regulerDays;
    }

    public ArrayList<HashMap<String, String>> getRouteModelList()
    {
        return RouteModelList;
    }

    public void setRouteModelList(String RouteModelList_data)
    {
        try
        {

            ArrayList<HashMap<String, String>> local_list = new ArrayList<>();

            JSONArray array = new JSONArray(RouteModelList_data);

            for (int i = 0; i < array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();

                map.put("Latitude", object.getString("Latitude"));
                map.put("Longitude", object.getString("Longitude"));
                map.put("Points", object.getString("Points"));
                map.put("RouteId", object.getString("RouteId"));

                local_list.add(map);
            }

            this.RouteModelList = local_list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<HashMap<String, String>> getRequestAcceptModelList()
    {
        return RequestAcceptModelList;
    }

    public void setRequestAcceptModelList(String requestAcceptModelList_data)
    {
        try
        {

            ArrayList<HashMap<String, String>> local_list = new ArrayList<>();

            JSONArray array = new JSONArray(requestAcceptModelList_data);

            for (int i = 0; i < array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();

                map.put(GlobalConstants.KeyNames.TripId.toString(), object.getString("TripId"));
                map.put(GlobalConstants.KeyNames.CustomerId.toString(), object.getString("CustomerId"));
                map.put(GlobalConstants.KeyNames.DriverId.toString(), object.getString("DriverId"));
                map.put(GlobalConstants.KeyNames.RequestId.toString(), object.getString("RequestId"));
                map.put(GlobalConstants.KeyNames.CustomerName.toString(), object.getString("CustomerName"));
                map.put(GlobalConstants.KeyNames.CustomerPhoto.toString(), object.getString("CustomerPhoto"));
                map.put(GlobalConstants.KeyNames.CustomerMobileNo.toString(), object.getString("CustomerMobileNo"));
                map.put(GlobalConstants.KeyNames.Flag.toString(), object.getString("Flag"));

                if (object.getString("CustomerId").equals(Ride_Details.my_customerID))
                {
                    setRequestId(object.getString("RequestId"));
                }

                local_list.add(map);
            }

            this.RequestAcceptModelList = local_list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public String getRoundTrip()
    {
        return RoundTrip;
    }

    public void setRoundTrip(String roundTrip)
    {
        RoundTrip = roundTrip;
    }

    public String getTripId()
    {
        return TripId;
    }

    public void setTripId(String tripId)
    {
        TripId = tripId;
    }

    public String getVehicleName()
    {
        return VehicleName;
    }

    public void setVehicleName(String vehicleName)
    {
        VehicleName = vehicleName;
    }

    public String getVehicleNo()
    {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo)
    {
        VehicleNo = vehicleNo;
    }

    public String getVehicleType()
    {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType)
    {
        VehicleType = vehicleType;
    }
}
