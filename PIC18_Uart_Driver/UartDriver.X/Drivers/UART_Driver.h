#ifndef UART_DRIVER_H
#define	UART_DRIVER_H

#ifdef	__cplusplus
extern "C" {
#endif
    
#include <stdbool.h>
#include <stdint.h>

/**
 * Data struct for reading data.
 */
typedef struct {
    char sender;
    char* command;
    char* message;
} UartData_t;
    
/**
* Initializes all the parameters to the default setting, as well as writing the
* tri-state registers. Initializes the UART to the default data rate and settings.
 * @param id The id of the device.
 * @param baud The baud rate.
*/
void uartInit(char id, uint16_t baud, void (*onReadDone)(UartData_t data));

/**
 * Enable the UART module
 * @param enable Enable or disable UART.
 */
void uartEnable(bool enable);

/**
 * Write data to the TX pin of UART module. 
 * @param command: Command
 * @param data: Date string to write
 */
void uartWrite(const char* command, const char* data);

/**
 * Read data from the RX pin of UART module.
 * @return data: returns the data struct.
 */
UartData_t uartRead();

#ifdef	__cplusplus
}
#endif

#endif	/* UART_DRIVER */