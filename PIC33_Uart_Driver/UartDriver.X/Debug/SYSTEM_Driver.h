/* 
 * File:   System functions
 * Author: wouter
 *
 * Created on 3 maart 2015, 13:06
 */

#ifndef SYSTEM_DRIVER_H
#define	SYSTEM_DRIVER_H

#ifdef	__cplusplus
extern "C" {
#endif
#include <libpic30.h>
#include <stdint.h>
#include <stdbool.h>

/* Microcontroller MIPs (FCY) */
#define SYS_FREQ        140000000L
#define FCY             SYS_FREQ/2

#define DelayMs(d) \
  { __delay32( (unsigned long) (((unsigned long long) d)*(FCY)/1000ULL)); }
#define DelayUs(d) \
  { __delay32( (unsigned long) (((unsigned long long) d)*(FCY)/1000000ULL)); }

/******************************************************************************/
/* System Function Prototypes                                                 */
/******************************************************************************/
    
/**
 * Configure oscillator settings.
 */
void sysInitOscillator(void); 

/**
 * Configure PLL settings.
 */
void sysInitPll(void); 

/**
 * Initialize the ports all as output, clear analog selections.
 */
void sysInitPorts(void);

/**
 * Initializes the interrupts to the default settings.
 */
void sysInitInterrupts(void);

/**
 * Enable all interrupts
 * @param enable
 */
void sysInterruptEnable(bool enable);


#endif