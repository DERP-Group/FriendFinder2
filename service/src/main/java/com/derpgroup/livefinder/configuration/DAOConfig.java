package com.derpgroup.livefinder.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.derpgroup.derpwizard.configuration.AccountLinkingDAOConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DAOConfig {

  @Valid
  @NotNull
  private AccountLinkingDAOConfig accountLinking;

  @JsonProperty
  public AccountLinkingDAOConfig getAccountLinking() {
    return accountLinking;
  }
  
  @JsonProperty
  public void setAccountLinking(AccountLinkingDAOConfig accountLinking) {
    this.accountLinking = accountLinking;
  }
}
