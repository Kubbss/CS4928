package com.cafepos.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderFSMTest {

    @Test
    void order_fsm_happy_path() {
        OrderFSM fsm = new OrderFSM();

        assertEquals("NEW", fsm.status());

        fsm.pay();
        assertEquals("PREPARING", fsm.status());

        fsm.markReady();
        assertEquals("READY", fsm.status());

        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
    }

    @Test
    void prepare_before_pay_does_not_leave_new_state() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());
        
        fsm.prepare();
        
        assertEquals("NEW", fsm.status());
    }

    @Test
    void cancel_from_new_moves_to_cancelled() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());

        fsm.cancel();

        assertEquals("CANCELLED", fsm.status());
    }
}

