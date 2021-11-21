package com.upgrade.CampsiteReservations.exceptions;

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
      notes = "Detailed description about the error, such as \"The booking period cannot be longer than three days.\""
  )
  private String errorDetails;

  @ApiModelProperty(
      notes = "HTTP status code, such as 400."
  )
  private int status;

}
