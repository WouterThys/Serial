#ifndef UART_DRIVER_H
#define	UART_DRIVER_H
    
#define TYPE_TEXT
//#define TYPE_SIMPLE_SHORT
//#define TYPE_SIMPLE_LONG
//#define TYPE_VARIABLE

/**
 * Data Struct for reading data.
 */

typedef struct {
    uint8_t sender; // Sender id
    char * command; // Command
    char * message; // Message
    uint8_t ack;    // Acknowledge
    uint8_t length; // Data length
} UartData_t;


/******************************************************************************/
/* System Function Prototypes                                                 */
/******************************************************************************/
/**
 * Initialize the UART module, select which module to use. The module is enabled
 * when it is initialized.
 * @param baud: Baud rate of the UART 
 */
void uartInit(uint16_t baud, void (*onReadDone)(UartData_t data));

/**
 * Enable the UART module
 * @param enable Enable or disable UART.
 */
void uartEnable(bool enable);

/**
 * 
 * @param data
 */
void uartWriteByte(uint8_t data);

/**
 * Write data to the TX pin of UART module. 
 * @param command: Command
 * @param data: Data string to write
 */
void uartWrite(const char* command, const char* data);

/**
 * Write data to the TX pin of UART module. 
 * @param command: Command
 * @param data: Data integer to write
 */
void uartWriteInt(const char* command, int data);


#endif