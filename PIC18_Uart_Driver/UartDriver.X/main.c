/*
 * File:   main.c
 * Author: wouter
 *
 * Created on October 22, 2016, 5:17 PM
 */

#include <xc.h>
#include <stdbool.h>
#include <stdint.h>
#include <string.h>

#include "Drivers/PORT_Driver.h"
#include "Drivers/UART_Driver.h"

#define _XTAL_FREQ 10000000

UartData_t read;

void main(void) {
    
    D_PORT_Init();
    // Initialize the UART module with a baud rate of 9600, with the use 
    // of interrupts.
    uartInit("Panda", 9600, true);
    uartEnable(true);
    
    uartWrite("I", "Init");
    while(1) {
        if(readReady) {
            readReady = false;
            read = uartRead();
            if (strcmp(read.command, "led") == 0) {
                if (strcmp(read.message, "on") == 0) {
                    PORTAbits.RA0 = 1;
                    uartWrite("Led", "I put it on");
                }
                if (strcmp(read.message,  "off") == 0) {
                    PORTAbits.RA0 = 0;
                    uartWrite("Led", "Putting led off sir");
                }
            }
        }    
    }
    return;
}
