package org.example.apitor.common.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record ProjectCreateRequestDTO(
     String projectName,
     @JsonProperty(value = "targetURL")
     String targetURL
){
}
