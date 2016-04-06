package com.derpgroup.livefinder.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.derpgroup.livefinder.model.accountlinking.ExternalAccountLink;

public class ExternalAccountLinkBiMap<K,V extends ExternalAccountLink> extends HashMap<K, V>{

  private static final long serialVersionUID = 8609550010568859945L;
  
  private Map<String,Set<V>> inverse = new HashMap<String, Set<V>>();
  
  @Override
  public V put(K k, V v){
    V output = super.put(k, v);
    
    String inverseKey = v.getUserId();
    if(inverseKey != null){
      if(inverse.containsKey(inverseKey)){
        inverse.get(inverseKey).add(v);
      }else{
        Set<V> values = new HashSet<V>();
        values.add(v);
        inverse.put(inverseKey, values);
      }
    }
    
    return output;
  }
  
  public Set<V> getKeysByUserId(String userId){
    return inverse.get(userId);
  }
  
  @Override
  public V remove(Object k){
    V output = super.remove(k);

    String inverseKey = output.getUserId();
    if(inverse.containsKey(inverseKey)){
      inverse.get(inverseKey).remove(output);
    }
    
    return output;
  }
}
