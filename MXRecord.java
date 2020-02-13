public class MXRecord {

    public String alias;
    public String pref;
    public int secondsCanCache;
    public boolean autoritive;

    public MXRecord() {

    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPref() {
        return pref;
    }

    public void setPref(String pref) {
        this.pref = pref;
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