package com.derpgroup.livefinder;

import java.util.LinkedList;
import java.util.Queue;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type", defaultImpl = LiveFinderMetadata.class)
public class LiveFinderMetadata extends CommonMetadata {
  
}
