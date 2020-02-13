public class NSRecord {

    public String alias;
    public int secondsCanCache;
    public boolean autoritive;

    public NSRecord(RData){

    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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