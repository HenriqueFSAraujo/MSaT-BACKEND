package com.montreal.msiav_bh.collection;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "file_notification_collection")
public class FileNotificationCollection {


    @Id
    private String id;

    @NotBlank
    private String receivedDate;

    @NotBlank
    private String fileEvidence;
}
