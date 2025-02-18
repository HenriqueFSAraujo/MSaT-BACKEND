package com.montreal.msiav_bh.collection;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "notification_collection")
public class NotificationCollection {


    @Id
    private String id;

    @NotNull
    private String electronicNotificationId;

    @NotNull
    private String arNotificationId;
}

