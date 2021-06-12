package com.arnaugarcia.ars.service.service.dto;

import com.arnaugarcia.ars.service.domain.DisplayOrientation;
import com.arnaugarcia.ars.service.service.exception.DeviceDataException;
import lombok.Getter;

import static com.arnaugarcia.ars.service.constants.DeviceConstants.PIPE_AND_HASHTAG;
import static java.lang.Double.parseDouble;
import static org.springframework.util.ObjectUtils.isEmpty;

@Getter
public class DeviceData {
    private Double pitch;
    private Double roll;

    public DeviceData(String data)  {
        String[] result = data.split(PIPE_AND_HASHTAG);
        try {
            if (!isEmpty(result)) {
                this.pitch = parseDouble(result[0]);
                this.roll = parseDouble(result[1]);
            }
        } catch (NumberFormatException exception) {
            throw new DeviceDataException();
        }

    }

    @Override
    public String toString() {
        return pitch + "|" + roll + "\n";
    }
}
