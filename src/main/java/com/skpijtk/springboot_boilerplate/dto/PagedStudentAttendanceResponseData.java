package com.skpijtk.springboot_boilerplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedStudentAttendanceResponseData {
    private List<StudentAttendanceDto> data;
    private long totalData;
    private int totalPage;
    private int currentPage;
    private int pageSize;
}