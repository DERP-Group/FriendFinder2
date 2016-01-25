package com.derpgroup.livefinder.model.accountlinking;

import com.derpgroup.derpwizard.voice.exception.DerpwizardException;

public class AccountLinkingNotLinkedException extends DerpwizardException {
  
  private static final long serialVersionUID = 6525505191876852627L;
  
  private InterfaceName interfaceName;
  
  public AccountLinkingNotLinkedException(InterfaceName interfaceName){
    super(null);
    this.interfaceName = interfaceName;
  }

  public InterfaceName getInterfaceName() {
    return interfaceName;
  }

  public void setInterfaceName(InterfaceName interfaceName) {
    this.interfaceName = interfaceName;
  }
}
