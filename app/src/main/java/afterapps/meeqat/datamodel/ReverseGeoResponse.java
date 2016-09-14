
package afterapps.meeqat.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//Generated("org.jsonschema2pojo")
public class ReverseGeoResponse {

    @SerializedName("dstOffset")
    @Expose
    private Integer dstOffset;
    @SerializedName("rawOffset")
    @Expose
    private Integer rawOffset;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timeZoneId")
    @Expose
    private String timeZoneId;
    @SerializedName("timeZoneName")
    @Expose
    private String timeZoneName;

    /**
     * 
     * @return
     *     The dstOffset
     */
    public Integer getDstOffset() {
        return dstOffset;
    }

    /**
     * 
     * @param dstOffset
     *     The dstOffset
     */
    public void setDstOffset(Integer dstOffset) {
        this.dstOffset = dstOffset;
    }

    /**
     * 
     * @return
     *     The rawOffset
     */
    public Integer getRawOffset() {
        return rawOffset;
    }

    /**
     * 
     * @param rawOffset
     *     The rawOffset
     */
    public void setRawOffset(Integer rawOffset) {
        this.rawOffset = rawOffset;
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
     *     The timeZoneId
     */
    public String getTimeZoneId() {
        return timeZoneId;
    }

    /**
     * 
     * @param timeZoneId
     *     The timeZoneId
     */
    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    /**
     * 
     * @return
     *     The timeZoneName
     */
    public String getTimeZoneName() {
        return timeZoneName;
    }

    /**
     * 
     * @param timeZoneName
     *     The timeZoneName
     */
    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

}
