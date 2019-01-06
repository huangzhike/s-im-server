package mmp.im.auth.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {

        Assert.hasLength(source, "text must be specified");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        Date date = null;
        try {
            date = dateFormat.parse(source);
        } catch (ParseException e) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(source);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return date;
    }

}
