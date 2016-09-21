
package afterapps.meeqat.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

//Generated("org.jsonschema2pojo")
public class PrayersResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Day> days = new ArrayList<Day>();

    /**
     * 
     * @return
     *     The code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 
     * @param code
     *     The code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The data
     */
    public List<Day> getDays() {
        return days;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setDays(List<Day> data) {
        this.days = data;
    }

}
