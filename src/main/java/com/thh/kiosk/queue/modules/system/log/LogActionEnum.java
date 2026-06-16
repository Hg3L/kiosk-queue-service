package com.thh.kiosk.queue.modules.system.log;

import com.thh.kiosk.queue.modules.system.log.dto.LogParamDto;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum LogActionEnum {

    /*
    * WebSocket
    * */
    WEBSOCKET_CONNECTED("WEBSOCKET_CONNECTED", LogComponent.LOCAL_SERVICE, "Đã kết nối WebSocket từ %s"),
    WEBSOCKET_MESSAGE_SENT("WEBSOCKET_MESSAGE_SENT", LogComponent.LOCAL_SERVICE, "Đã gửi dữ liệu qua WebSocket: %s"),

    KIOSK_AUDIO_PLAY("KIOSK_AUDIO_PLAY", LogComponent.LOCAL_SERVICE, "Đã phát âm thanh tại kiosk"),

    COUNTER_CREATED("CREATE_COUNTER", LogComponent.KIOSK_ADMIN, "Đã tạo quầy dịch vụ mới: %s"),
    COUNTER_UPDATED("UPDATE_COUNTER", LogComponent.KIOSK_ADMIN, "Đã cập nhật quầy dịch vụ với ID: %s"),
    COUNTER_DELETED("DELETE_COUNTER", LogComponent.KIOSK_ADMIN, "Đã xóa quầy dịch vụ ID: %s"),

    TICKET_CREATED("TICKET_CREATED", LogComponent.LOCAL_SERVICE, "Đã tạo vé mới: %s"),
    TICKET_UPDATE("TICKET_UPDATE", LogComponent.LOCAL_SERVICE, "Cập nhật trạng thái vé với ID: %s"),

    AUDIO_DEVICE_NOT_FOUND("AUDIO_DEVICE_NOT_FOUND", LogComponent.LOCAL_SERVICE, "Thiết bị âm thanh bị ngắt kết nối"),
    AUDIO_FILE_NOT_FOUND("AUDIO_FILE_NOT_FOUND", LogComponent.LOCAL_SERVICE, "Không tìm thấy file âm thanh"),
    AUDIO_CALLING("AUDIO_CALLING", LogComponent.LOCAL_SERVICE, "Đang phát âm thanh gọi khách hàng tại %s")



    ;

    final String code;
    final LogComponent component;
    final String messageTemplate;

    public LogParamDto buildParam(String formatArg, Map<String, Object> details) {
        return LogParamDto.builder()
                .component(this.getComponent())
                .action(this.getCode())
                .message(String.format(this.getMessageTemplate(), formatArg))
                .details(details)
                .build();
    }
}
