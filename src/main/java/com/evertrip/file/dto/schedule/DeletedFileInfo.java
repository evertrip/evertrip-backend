package com.evertrip.file.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeletedFileInfo {

    private Long id;

    private Long fileId;
}
