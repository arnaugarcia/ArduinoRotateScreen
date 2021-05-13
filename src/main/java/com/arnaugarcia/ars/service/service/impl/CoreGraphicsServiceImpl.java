package com.arnaugarcia.ars.service.service.impl;

import com.arnaugarcia.ars.service.domain.Display;
import com.arnaugarcia.ars.service.domain.ScreenRotation;
import com.arnaugarcia.ars.service.repository.CoreGraphicsRepository;
import com.arnaugarcia.ars.service.repository.types.CGDirectDisplayID;
import com.arnaugarcia.ars.service.repository.types.int32_t;
import com.arnaugarcia.ars.service.service.CoreGraphicsService;
import com.arnaugarcia.ars.service.service.exception.EmptyDisplayException;
import com.sun.jna.platform.mac.IOKit;

import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class CoreGraphicsServiceImpl implements CoreGraphicsService {

    private final Integer MAX_DISPLAYS = 20;

    @Override
    public List<Display> findDisplays() {
        CGDirectDisplayID[] displayIDS = new CGDirectDisplayID[MAX_DISPLAYS];
        if (CoreGraphicsRepository.INSTANCE.CGGetOnlineDisplayList(MAX_DISPLAYS, displayIDS, null).isError()) {
            throw new EmptyDisplayException();
        }
        return stream(displayIDS)
                .filter(CGDirectDisplayID::isNotEmpty)
                .map(buildDisplay())
                .collect(toList());
    }

    @Override
    public void rotateScreen(Display display, ScreenRotation orientation) {
        final IOKit.IOService ioService = CoreGraphicsRepository.INSTANCE.CGDisplayIOServicePort(new CGDirectDisplayID(display.getId()));
        CoreGraphicsRepository.INSTANCE.IOServiceRequestProbe(ioService, new int32_t(orientation.getValue()));
    }

    private Integer getScreenOrientation(CGDirectDisplayID display) {
        return CoreGraphicsRepository.INSTANCE.CGDisplayRotation(display).intValue();
    }

    private Function<CGDirectDisplayID, Display> buildDisplay() {
        return displayID -> Display.builder()
                .id(displayID.intValue())
                .height(CoreGraphicsRepository.INSTANCE.CGDisplayPixelsHigh(displayID).intValue())
                .wide(CoreGraphicsRepository.INSTANCE.CGDisplayPixelsWide(displayID).intValue())
                .main(getMainDisplayID().equals(displayID))
                .orientation(getScreenOrientation(displayID))
                .active(displayIsActive(displayID))
                .build();
    }
    private Boolean displayIsActive(CGDirectDisplayID displayID) {
        return CoreGraphicsRepository.INSTANCE.CGDisplayIsActive(displayID).isTrue();
    }

    private CGDirectDisplayID getMainDisplayID() {
        return CoreGraphicsRepository.INSTANCE.CGMainDisplayID();
    }
}
