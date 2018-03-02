#ifndef PORT_DRIVER_H
#define	PORT_DRIVER_H

#ifdef	__cplusplus
extern "C" {
#endif

    
    // Ports for UART
#define UART_TX         PORTCbits.RC7
#define UART_RX         PORTCbits.RC6
    
#define UART_TX_Dir     TRISCbits.TRISC7
#define UART_RX_Dir     TRISCbits.TRISC6
    
 /**
 * Initializes all the parameters to the default setting, as well as writing the
 * tri-state registers. Sets all ports to zero, and all tri-states to output.
 */
void D_PORT_Init();

#ifdef	__cplusplus
}
#endif

#endif	/* PORT_DRIVER */