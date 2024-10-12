package Responses;

import java.sql.Date;

public class LogentriesResponse {
    private int logeintrag_id;
    private String logeintrag_beschreibung;
    private Date gefunden_am;
    private String logfile_name;
    private String pattern_name;
    private String schweregrad;

    public void setLogeintrag_id(int logeintrag_id) {
        this.logeintrag_id = logeintrag_id;
    }

    public int getLogeintrag_id() {
        return logeintrag_id;
    }

    public void setLogeintrag_beschreibung(String logeintrag_beschreibung) {
        this.logeintrag_beschreibung = logeintrag_beschreibung;
    }

    public String getLogeintrag_beschreibung() {
        return logeintrag_beschreibung;
    }

    public void setGefunden_am(Date gefunden_am) {
        this.gefunden_am = gefunden_am;
    }

    public Date getGefunden_am() {
        return gefunden_am;
    }

    public void setLogfile_name(String logfile_name) {
        this.logfile_name = logfile_name;
    }

    public String getLogfile_name() {
        return logfile_name;
    }

    public void setPattern_name(String pattern_name) {
        this.pattern_name = pattern_name;
    }

    public String getPattern_name() {
        return pattern_name;
    }

    public void setSchweregrad(String schweregrad) {
        this.schweregrad = schweregrad;
    }

    public String getSchweregrad() {
        return schweregrad;
    }
}
