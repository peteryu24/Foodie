package com.sparta.tl3p.backend.order;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.member.enums.Role;
import com.sparta.tl3p.backend.domain.member.repository.MemberRepository;
import com.sparta.tl3p.backend.domain.order.dto.OrderCancelRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderDetailResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderItemRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderUpdateRequestDto;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.repository.OrderRepository;
import com.sparta.tl3p.backend.domain.order.service.OrderService;
import com.sparta.tl3p.backend.domain.payment.dto.PaymentRequestDto;
import com.sparta.tl3p.backend.domain.payment.dto.PaymentResponseDto;
import com.sparta.tl3p.backend.domain.payment.entity.Payment;
import com.sparta.tl3p.backend.domain.payment.enums.PaymentStatus;
import com.sparta.tl3p.backend.domain.payment.service.PaymentService;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ItemRepository itemRepository;

    // 공통 목 객체
    private Member customer;
    private Member owner;
    private Member anotherMember;
    private Store store;
    private OrderRequestDto orderRequestDto;
    private OrderUpdateRequestDto orderUpdateRequestDto;
    private OrderCancelRequestDto orderCancelRequestDto;
    private UUID orderId;
    private PaymentResponseDto paymentResponse;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        // 고객(로그인 사용자) 설정
        customer = mock(Member.class);
        when(customer.getMemberId()).thenReturn(1L);
        when(customer.getRole()).thenReturn(Role.CUSTOMER);

        // 점주 설정
        owner = mock(Member.class);
        when(owner.getMemberId()).thenReturn(2L);
        when(owner.getRole()).thenReturn(Role.OWNER);

        // 다른 회원(접근 권한 테스트용)
        anotherMember = mock(Member.class);
        when(anotherMember.getMemberId()).thenReturn(3L);
        when(anotherMember.getRole()).thenReturn(Role.CUSTOMER);

        // 가게 설정 (기본 소유자는 owner)
        store = mock(Store.class);
        when(store.getMember()).thenReturn(owner);

        // 주문 요청 DTO 기본 스텁 설정
        orderRequestDto = mock(OrderRequestDto.class);
        UUID storeId = UUID.randomUUID();
        when(orderRequestDto.getStoreId()).thenReturn(storeId);
        when(orderRequestDto.getOrderType()).thenReturn(OrderType.ONLINE);
        when(orderRequestDto.getPaymentMethod()).thenReturn(null); // PaymentService 내부 사용
        when(orderRequestDto.getDeliveryAddress())
                .thenReturn(new Address("Seoul", "Main Street", "12345"));
        when(orderRequestDto.getStoreRequest()).thenReturn("테스트 요청");

        // 주문 아이템 설정 (단일 아이템)
        OrderItemRequestDto orderItem = mock(OrderItemRequestDto.class);
        UUID itemId = UUID.randomUUID();
        when(orderItem.getItemId()).thenReturn(itemId);
        when(orderItem.getQuantity()).thenReturn(2);
        when(orderRequestDto.getItems()).thenReturn(Collections.singletonList(orderItem));

        // 아이템 조회 스텁 (정상 케이스)
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(BigDecimal.valueOf(1000));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // 회원 및 가게 조회 스텁 (정상 케이스)
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // 결제 서비스 스텁 (기본 성공)
        paymentResponse = mock(PaymentResponseDto.class);
        when(paymentResponse.getPaymentStatus()).thenReturn(PaymentStatus.SUCCESS);
        Payment payment = mock(Payment.class);
        when(paymentResponse.toPayment()).thenReturn(payment);
        when(paymentService.requestPayment(any(Order.class), any(PaymentRequestDto.class)))
                .thenReturn(paymentResponse);

        // 주문 저장 시 orderId 세팅
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(orderId);
            return o;
        });

        // 기타 DTO 목
        orderUpdateRequestDto = mock(OrderUpdateRequestDto.class);
        orderCancelRequestDto = mock(OrderCancelRequestDto.class);
    }

    // ================= createOrder 테스트 =================

    @Test
    @DisplayName("주문 생성 성공 - 정상 케이스")
    void createOrder_success() {
        // when: 주문 생성 호출 (결제 성공)
        OrderResponseDto response = orderService.createOrder(orderRequestDto, 1L);

        // then: 응답의 orderId가 설정되어 있어야 함
        assertThat(response.getOrderId()).isEqualTo(orderId);
        verify(paymentService).requestPayment(any(Order.class), any(PaymentRequestDto.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 결제 실패")
    void createOrder_paymentFailure_throwsException() {
        // given: 결제 실패 상황 재현
        when(paymentResponse.getPaymentStatus()).thenReturn(PaymentStatus.FAILED);

        // when & then: BusinessException 발생
        assertThatThrownBy(() -> orderService.createOrder(orderRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PAYMENT_FAILED.getMessage());
    }

    @Test
    @DisplayName("주문 생성 실패 - 회원 미존재")
    void createOrder_memberNotFound_throwsException() {
        // given: 회원 조회 시 empty 반환
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then: BusinessException 발생
        assertThatThrownBy(() -> orderService.createOrder(orderRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("주문 생성 실패 - 가게 미존재")
    void createOrder_storeNotFound_throwsException() {
        // given: 가게 조회 시 empty 반환
        when(storeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // when & then: BusinessException 발생
        assertThatThrownBy(() -> orderService.createOrder(orderRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.STORE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 미존재")
    void createOrder_itemNotFound_throwsException() {
        // given: 주문 아이템에 해당하는 상품이 없을 경우
        OrderItemRequestDto missingItem = mock(OrderItemRequestDto.class);
        UUID missingItemId = UUID.randomUUID();
        when(missingItem.getItemId()).thenReturn(missingItemId);
        when(missingItem.getQuantity()).thenReturn(3);
        when(orderRequestDto.getItems()).thenReturn(Collections.singletonList(missingItem));
        when(itemRepository.findById(missingItemId)).thenReturn(Optional.empty());

        // when & then: BusinessException 발생
        assertThatThrownBy(() -> orderService.createOrder(orderRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ITEM_NOT_FOUND.getMessage());
    }

    // ================= updateOrder 테스트 =================

    @Test
    @DisplayName("고객 주문 수정 성공 - 시간 내 수정 가능")
    void updateOrder_customer_successWithinTime() {
        // given: 주문 생성 후 현재 시간 내 수정 가능한 상태
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        // 주문 소유자(customer)는 생성자에서 할당된 값이 사용됨
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        OrderResponseDto response = orderService.updateOrder(orderId, orderUpdateRequestDto, 1L);
        // then: update 처리 후 반환된 orderId가 동일한지만 검증
        assertThat(response.getOrderId()).isEqualTo(existingOrder.getOrderId());
    }

    @Test
    @DisplayName("고객 주문 수정 실패 - 수정 시간 초과")
    void updateOrder_customer_timeOut_throwsException() {
        // given: 주문 생성 시간이 6분 전으로 설정되어 수정 시간 초과 상태
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now().minusMinutes(6));
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));

        // when & then: BusinessException 발생 (시간 초과)
        assertThatThrownBy(() -> orderService.updateOrder(orderId, orderUpdateRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ORDER_TIME_OUT.getMessage());
    }

    @Test
    @DisplayName("고객 주문 수정 실패 - 접근 권한 없음")
    void updateOrder_customer_accessDenied() {
        // given: 주문 소유자가 고객이 아닌 경우
        Order existingOrder = spy(new Order(orderRequestDto, anotherMember, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(anotherMember).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));

        // when & then: BusinessException 발생 (접근 권한 없음)
        assertThatThrownBy(() -> orderService.updateOrder(orderId, orderUpdateRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("점주 주문 수정 성공 - 점주 권한으로 수정")
    void updateOrder_owner_success() {
        // given: 주문은 고객이 생성했지만, 점주(owner)가 수정 가능한 상황 (시간 제한 무시)
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        OrderResponseDto response = orderService.updateOrder(orderId, orderUpdateRequestDto, 2L);

        // then: update 처리 후 반환된 orderId가 동일한지만 검증
        assertThat(response.getOrderId()).isEqualTo(existingOrder.getOrderId());
    }

    @Test
    @DisplayName("점주 주문 수정 실패 - 접근 권한 없음")
    void updateOrder_owner_accessDenied() {
        // given: 가게 실제 소유주가 아닌 점주가 수정 요청할 경우
        when(store.getMember()).thenReturn(anotherMember); // 실제 가게 소유자는 anotherMember
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(owner));

        // when & then: BusinessException 발생 (접근 권한 없음)
        assertThatThrownBy(() -> orderService.updateOrder(orderId, orderUpdateRequestDto, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    // ================= cancelOrder 테스트 =================

    @Test
    @DisplayName("고객 주문 취소 성공 - 시간 내 취소 가능")
    void cancelOrder_customer_successWithinTime() {
        // given: 주문 생성 시간이 현재로 설정되어 취소 가능 상태
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        // when: 고객이 주문 취소 요청
        OrderResponseDto response = orderService.cancelOrder(orderId, orderCancelRequestDto, 1L);

        // then: 취소 메서드 호출 및 응답 확인
        verify(existingOrder).cancelOrder();
        assertThat(response.getOrderId()).isEqualTo(existingOrder.getOrderId());
    }

    @Test
    @DisplayName("고객 주문 취소 실패 - 취소 시간 초과")
    void cancelOrder_customer_timeOut_throwsException() {
        // given: 주문 생성 시간이 6분 전으로 설정되어 취소 불가 상태
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now().minusMinutes(6));
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));

        // when & then: BusinessException 발생 (시간 초과)
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, orderCancelRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ORDER_TIME_OUT.getMessage());
    }

    @Test
    @DisplayName("고객 주문 취소 실패 - 접근 권한 없음")
    void cancelOrder_customer_accessDenied() {
        // given: 주문 소유자가 고객이 아닌 경우
        Order existingOrder = spy(new Order(orderRequestDto, anotherMember, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(anotherMember).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));

        // when & then: BusinessException 발생 (접근 권한 없음)
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, orderCancelRequestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("점주 주문 취소 성공 - 점주 권한으로 취소")
    void cancelOrder_owner_success() {
        // given: 가게의 실제 소유주(owner)가 취소 요청 (시간 제한 무시)
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        // when: 점주가 주문 취소 요청
        OrderResponseDto response = orderService.cancelOrder(orderId, orderCancelRequestDto, 2L);

        // then: 취소 성공 확인
        verify(existingOrder).cancelOrder();
        assertThat(response.getOrderId()).isEqualTo(existingOrder.getOrderId());
    }

    @Test
    @DisplayName("점주 주문 취소 실패 - 접근 권한 없음")
    void cancelOrder_owner_accessDenied() {
        // given: 가게의 실제 소유주가 아닌 점주가 취소 요청할 경우
        when(store.getMember()).thenReturn(anotherMember); // 실제 가게 소유자는 anotherMember
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(owner));

        // when & then: BusinessException 발생 (접근 권한 없음)
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, orderCancelRequestDto, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    // ================= getOrderDetail 테스트 =================

    @Test
    @DisplayName("고객 주문 상세 조회 성공")
    void getOrderDetail_customer_success() {
        // given: 고객이 생성한 주문의 경우
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));

        // when: 고객이 주문 상세 조회 요청
        OrderDetailResponseDto detail = orderService.getOrderDetail(orderId, 1L);

        // then: 주문 상세 정보가 반환됨
        assertThat(detail).isNotNull();
    }

    @Test
    @DisplayName("고객 주문 상세 조회 실패 - 접근 권한 없음")
    void getOrderDetail_customer_accessDenied() {
        // given: 주문 소유자가 고객이 아닌 경우
        Order existingOrder = spy(new Order(orderRequestDto, anotherMember, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(anotherMember).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(customer));

        // when & then: BusinessException 발생 (접근 권한 없음)
        assertThatThrownBy(() -> orderService.getOrderDetail(orderId, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("점주 주문 상세 조회 성공")
    void getOrderDetail_owner_success() {
        // given: 가게의 실제 소유주가 주문 상세 조회 요청하는 경우
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(owner));

        // when: 점주가 주문 상세 조회 요청
        OrderDetailResponseDto detail = orderService.getOrderDetail(orderId, 2L);

        // then: 주문 상세 정보가 반환됨
        assertThat(detail).isNotNull();
    }

    @Test
    @DisplayName("점주 주문 상세 조회 실패 - 접근 권한 없음")
    void getOrderDetail_owner_accessDenied() {
        // given: 가게 실제 소유주와 다른 점주가 조회 요청 시
        when(store.getMember()).thenReturn(anotherMember); // 실제 가게 소유자는 anotherMember
        Order existingOrder = spy(new Order(orderRequestDto, customer, store));
        existingOrder.setCreatedAt(LocalDateTime.now());
        doReturn(customer).when(existingOrder).getMember();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(owner));

        // when & then: BusinessException 발생 (접근 권한 없음)
        assertThatThrownBy(() -> orderService.getOrderDetail(orderId, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    // ================= 조회 메서드 테스트 =================

    @Test
    @DisplayName("사용자 주문 목록 조회 성공")
    void getUserOrders_success() {
        // given: 특정 사용자의 주문 목록 조회
        Order existingOrder = new Order(orderRequestDto, customer, store);
        when(orderRepository.findByMemberMemberId(1L))
                .thenReturn(Collections.singletonList(existingOrder));

        // when: 사용자 주문 목록 요청 (반환 타입이 OrderResponseDto)
        List<OrderResponseDto> orders = orderService.getUserOrders(1L);

        // then: 목록에 1건 이상 존재해야 함
        assertThat(orders).hasSize(1);
    }

    @Test
    @DisplayName("점주 가게 주문 목록 조회 성공")
    void getStoreOrders_success() {
        // given: 가게의 실제 소유주가 조회하는 경우
        when(store.getMember()).thenReturn(owner);
        Order existingOrder = new Order(orderRequestDto, customer, store);
        when(storeRepository.findById(any(UUID.class))).thenReturn(Optional.of(store));
        when(orderRepository.findByStoreStoreId(any(UUID.class)))
                .thenReturn(Collections.singletonList(existingOrder));

        // when: 가게 주문 목록 요청 (반환 타입이 OrderResponseDto)
        List<OrderResponseDto> orders = orderService.getStoreOrders(UUID.randomUUID(), 2L);

        // then: 목록에 1건 이상 존재해야 함
        assertThat(orders).hasSize(1);
    }

    @Test
    @DisplayName("점주 가게 주문 목록 조회 실패 - 접근 권한 없음")
    void getStoreOrders_accessDenied() {
        // given: 가게의 실제 소유주가 아닌 점주가 조회하는 경우
        when(store.getMember()).thenReturn(anotherMember);
        when(storeRepository.findById(any(UUID.class))).thenReturn(Optional.of(store));

        // when & then: BusinessException 발생 (접근 권한 없음)
        assertThatThrownBy(() -> orderService.getStoreOrders(UUID.randomUUID(), 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("주문 검색 성공")
    void searchOrders_success() {
        // given: 특정 검색 조건에 해당하는 주문이 존재하는 경우
        Order existingOrder = new Order(orderRequestDto, customer, store);
        when(orderRepository.searchOrders(1L, "TestStore", "TestProduct"))
                .thenReturn(Collections.singletonList(existingOrder));

        // when: 주문 검색 요청
        List<OrderResponseDto> orders = orderService.searchOrders(1L, "TestStore", "TestProduct");

        // then: 검색 결과에 1건 이상 존재함
        assertThat(orders).hasSize(1);
    }
}
