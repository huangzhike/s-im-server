package mmp.im.gate.web.converter;

import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToStringConverter implements Converter<Date, String> {

    @Override
    public String convert(Date source) {
        return (source == null) ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(source);
    }

}
