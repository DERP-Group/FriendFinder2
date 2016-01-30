package com.derpgroup.livefinder.model.accountlinking;

import com.derpgroup.livefinder.resource.AuthResource;

public class LandingPageErrorResponse{
  /**
   * 
   */
  private final AuthResource LandingPageErrorResponse;
  private String error;
  
  public LandingPageErrorResponse(AuthResource authResource, String error){
    LandingPageErrorResponse = authResource;
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}