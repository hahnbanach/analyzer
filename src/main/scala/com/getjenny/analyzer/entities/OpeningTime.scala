package com.getjenny.analyzer.entities

/**
  * Created by angelo on 13/02/2019.
  */

case class OpeningTime(
                        openTime: String, /** “HH:MM”, 24 h format */
                        closeTime: String, /** “HH:MM”, 24 h format */
                        timezone: String, /** any of UTC, GMT, UT, CET, UTC+<N>, UTC-<N>, GMT+<N> */
                        months: Set[Int], /** e.g. {1,2,3}, // set of months 1 to 12 */
                        days: Set[Int], /** e.g. {1,2,3}, // set of days 1 to 31 */
                        weekDays: Set[Int] /** e.g. {1,2,3,4,5} // set of weekdays 1(Monday) - 7(Sunday) */
                      )
