/**
 *  HEM Monthly Reset
 *
 *  Copyright 2017
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 */

definition(
    name: "HEM Monthly Reset",,
    namespace: "jasonrwise77",
    author: "Jason Wise",
    description: "Resets the HEM on a specified day/time of every month",
    parent: "jasonrwise77:HEM Reset Manager",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/jasonrwise77/My-SmartThings/master/icons/HEM%20Monthly.png",
    iconX2Url: "https://raw.githubusercontent.com/jasonrwise77/My-SmartThings/master/icons/HEM%20Monthly.png",
    iconX3Url: "https://raw.githubusercontent.com/jasonrwise77/My-SmartThings/master/icons/HEM%20Monthly.png"
    )

preferences {
    section("Choose HEMs to reset:") {
        input(name: "meter", type: "capability.energyMeter", title: "Which HEMs To Reset?", description: null, required: true, submitOnChange: true)
    }    
    section("Time of Day To Reset") {
        input "time", "time", title: "Select A Time Of Day"
    }    
    section("Day Of The Month To Reset") {
        input "day", "number", title: "Select A Day Of The Month"
    }
}

def installed() {
	log.debug "Aeon HEM v1 Reset Manager SmartApp installed, now preparing to schedule the first reset."
}

def updated() {
	log.debug "Aeon HEM v1 Reset Manager SmartApp updated, so update the user defined schedule and schedule another check for the next day."
	unschedule()
    def scheduleTime = timeToday(time, location.timeZone)
    def timeNow = now()
    log.debug "Current time is ${(new Date(timeNow)).format("EEE MMM dd yyyy HH:mm z", location.timeZone)}"
    log.debug "Scheduling meter reset check at ${scheduleTime.format("EEE MMM dd yyyy HH:mm z", location.timeZone)}"
    schedule(scheduleTime, resetTheMeter)
}

def initialize() {
	unschedule()
    def scheduleTime = timeToday(time, location.timeZone)
    def timeNow = now()
    log.debug "Current time is ${(new Date(timeNow)).format("EEE MMM dd yyyy HH:mm z", location.timeZone)}"
    scheduleTime = scheduleTime + 1 // Next day schedule
    log.debug "Scheduling next meter reset check at ${scheduleTime.format("EEE MMM dd yyyy HH:mm z", location.timeZone)}"
    schedule(scheduleTime, resetTheMeter)
}

def resetTheMeter() {
    Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
    def currentDayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH);
    log.debug "Aeon HEM v1 meter reset schedule triggered..."
    log.debug "...checking for the day of month requested by the user"
    log.debug "...the day of the month right now is ${currentDayOfMonth}"
    log.debug "...the day the user requested a reset is ${day}"
    if (currentDayOfMonth == day) {
        log.debug "...resetting the meter because it's when the user requested it."
        meter.resetMeter()
    } else {
        log.debug "...meter reset not scheduled for today because it's not when the user requested it."
    }
    log.debug "Process completed, now schedule the reset to check on the next day."
    initialize()
}
