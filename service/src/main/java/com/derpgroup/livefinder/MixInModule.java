package com.derpgroup.livefinder;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MixInModule extends SimpleModule {
  
  public MixInModule(){
    super("LiveFinderModule"); 
  }
  
  @Override
   public void setupModule(SetupContext context)
     {
       context.setMixInAnnotations(CommonMetadata.class, CommonMetadataMixIn.class);
    }
}
