public class ARecord {

    public String IPAddress;
    public int secondsCanCache;
    public boolean autoritive;

    public ARecord() {

    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String iPAddress) {
        IPAddress = iPAddress;
    }

    public int getSecondsCanCache() {
        return secondsCanCache;
    }

    public void setSecondsCanCache(int secondsCanCache) {
        this.secondsCanCache = secondsCanCache;
    }

    public boolean isAutoritive() {
        return autoritive;
    }

    public void setAutoritive(boolean autoritive) {
        this.autoritive = autoritive;
    }

}