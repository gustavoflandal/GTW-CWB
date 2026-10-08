package muralha.digital.monitorado;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String v) {
        return (v != null && !v.isEmpty()) ? LocalTime.parse(v) : null;
    }

    @Override
    public String marshal(LocalTime v) {
        return (v != null) ? v.toString() : null;
    }
}
