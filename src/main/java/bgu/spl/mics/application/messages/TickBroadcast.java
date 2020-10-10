package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
 int currentTIck;

 public TickBroadcast(int currentTIck) {
 this.currentTIck =currentTIck;
 }

 public int getCurrentTIck() {
  return currentTIck;
 }

}
