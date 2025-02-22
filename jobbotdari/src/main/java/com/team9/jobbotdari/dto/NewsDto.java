package com.team9.jobbotdari.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NewsDto {
    private String title;
    private String link;
    private Date publishedDate;
}
