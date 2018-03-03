#include <xc.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>        /* Includes uint16_t definition                    */
#include <stdbool.h>       /* Includes true/false definition                  */
#include <string.h>

#include "Settings.h"
#include "Debug/SYSTEM_Driver.h"
#include "Debug/UART_Driver.h"


/*******************************************************************************
 *          DEFINES
 ******************************************************************************/

/*******************************************************************************
 *          MACRO FUNCTIONS
 ******************************************************************************/

/*******************************************************************************
 *          DEFINES
 ******************************************************************************/

/*******************************************************************************
 *          VARIABLES
 ******************************************************************************/

/*******************************************************************************
 *          LOCAL FUNCTIONS
 ******************************************************************************/
static void initialize();

void initialize() {
    sysInterruptEnable(false);
    
    // Initialize system
    sysInitPll();
    sysInitOscillator();
    sysInitPorts();
    
    // Interrupts
    sysInitInterrupts();
    sysInterruptEnable(true);
}

/*******************************************************************************
 *          VARIABLES
 ******************************************************************************/

void onReadDone(UartData_t data) {
    LED1 = !LED1;
    
    printf(data.message);
}

/*******************************************************************************
 *          MAIN PROGRAM
 ******************************************************************************/

int main(void) {

    initialize();
    
    
    uartInit(UART1_BAUD, &onReadDone);
    uartEnable(true);
    
    DelayMs(100);
    
    printf("start");
    
    while(1) {
    
        
        
    }
}


