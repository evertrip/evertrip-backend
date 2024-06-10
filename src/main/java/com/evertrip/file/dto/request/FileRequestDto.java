package com.evertrip.file.dto.request;

import com.evertrip.file.common.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDto {

    private TableName tableName;

    private Long tableKey;

    public static FileRequestDto create(TableName tableName, Long tableKey) {
        return FileRequestDto.builder().tableName(tableName).tableKey(tableKey).build();
    }
}
