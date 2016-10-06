
package afterapps.miqat.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//Generated("org.jsonschema2pojo")
public class Day {

    @SerializedName("timings")
    @Expose
    private Timings timings;
    @SerializedName("date")
    @Expose
    private Date date;

    /**
     * 
     * @return
     *     The timings
     */
    public Timings getTimings() {
        return timings;
    }

    /**
     * 
     * @param timings
     *     The timings
     */
    public void setTimings(Timings timings) {
        this.timings = timings;
    }

    /**
     * 
     * @return
     *     The date
     */
    public Date getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    public void setDate(Date date) {
        this.date = date;
    }

}
