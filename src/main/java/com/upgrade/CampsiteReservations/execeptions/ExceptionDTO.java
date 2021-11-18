package com.upgrade.CampsiteReservations.execeptions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionDTO {

  @ApiModelProperty(
      notes = "High-level error, such as Invalid Input."
  )
  private String error;
  @ApiModelProperty(
      notes = "Detailed description about the error, such as \"Age may not be negative.\""
  )
  private String errorDetails;
  @ApiModelProperty(
      notes = "High-level error code as an enumeration, such as INVALID_AGE."
  )
  private String errorCode;
  @ApiModelProperty(
      notes = "HTTP status code, such as 400."
  )
  private int status;

}
