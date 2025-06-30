package com.cabybara.aishortvideo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageResponseDetail<T>{
    private int pageNo;
    private int pageSize;
    private int totalPage;
    private Long totalElements;
    private T items;
}
