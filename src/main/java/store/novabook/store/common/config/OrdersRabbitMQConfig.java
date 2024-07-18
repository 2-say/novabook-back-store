package store.novabook.store.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdersRabbitMQConfig {

	// 공통 EXCHANGE
	@Bean
	public Exchange sagaExchange() {
		return ExchangeBuilder.directExchange("nova.orders.saga.exchange").build();
	}

	@Bean
	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}

	// QUEUES
	@Bean
	public Queue ordersVerifyFormQueue() {
		return QueueBuilder.durable("nova.orders.form.verify.queue").build();
	}

	@Bean
	public Queue ordersDecrementPointQueue() {
		return QueueBuilder.durable("nova.point.decrement.queue").build();
	}

	@Bean
	public Queue ordersApplyCouponQueue() {
		return QueueBuilder.durable("nova.coupon.apply.queue").build();
	}

	@Bean
	public Queue ordersPaymentApproveQueue() {
		return QueueBuilder.durable("nova.orders.approve.payment.queue").build();
	}

	@Bean
	public Queue ordersSaveOrderDatabaseQueue() {
		return QueueBuilder.durable("nova.orders.save.orders.database.queue").build();
	}

	@Bean
	public Queue pointEarnQueue() {
		return QueueBuilder.durable("nova.point.earn.queue").build();
	}

	@Bean
	public Queue cartDeleteQueue() {
		return QueueBuilder.durable("nova.cart.delete.queue").build();
	}


	//TOSS PAYMENT 결제 취소 QUEUE
	@Bean
	public Queue paymentCancelQueue() {
		return QueueBuilder.durable("nova.payment.cancel.queue").build();
	}

	// 결제 취소 포인트 환불 QUEUE
	@Bean
	public Queue pointRequestPayAmountQueue() {
		return QueueBuilder.durable("nova.point.request.pay.cancel.queue").build();
	}

	// 결제취소 시작하는 QUEUE
	@Bean
	public Queue requestPayCancelQueue() {
		return QueueBuilder.durable("nova.request.pay.cancel.queue").build();
	}


	// 결제 취소 재고처리하는 QUEUE
	@Bean
	public Queue ordersRequestPayCancelQueue() {
		return QueueBuilder.durable("nova.orders.request.pay.cancel.queue").build();
	}



	/*보상 트랜잭션 큐*/
	@Bean
	public Queue compensateOrdersConfirmFormQueue() {
		return QueueBuilder.durable("nova.orders.compensate.form.confirm.queue").build();
	}

	@Bean
	public Queue compensateOrdersDecrementPointQueue() {
		return QueueBuilder.durable("nova.point.compensate.decrement.queue").build();
	}

	@Bean
	public Queue compensateOrdersApplyCouponQueue() {
		return QueueBuilder.durable("nova.coupon.compensate.apply.queue").build();
	}

	@Bean
	public Queue compensateApprovePaymentQueue() {
		return QueueBuilder.durable("nova.orders.compensate.approve.payment.queue").build();
	}

	// Dead queue
	@Bean
	public Queue deadOrdersSagaQueue() {
		return QueueBuilder.durable("nova.orders.saga.dead.queue").build();
	}

	// SAGA QUEUE
	@Bean
	public Queue api1ProducerQueue() {
		return QueueBuilder.durable("nova.api1-producer-queue").build();
	}

	@Bean
	public Queue api2ProducerQueue() {
		return QueueBuilder.durable("nova.api2-producer-queue").build();
	}

	@Bean
	public Queue api3ProducerQueue() {
		return QueueBuilder.durable("nova.api3-producer-queue").build();
	}

	@Bean
	public Queue api4ProducerQueue() {
		return QueueBuilder.durable("nova.api4-producer-queue").build();
	}

	@Bean
	public Queue api5ProducerQueue() {
		return QueueBuilder.durable("nova.api5-producer-queue").build();
	}

	@Bean
	public Queue api6ProducerQueue() {
		return QueueBuilder.durable("nova.api6-producer-queue").build();
	}

	// BINDING
	@Bean
	public Binding ordersConfirmBinding(Queue ordersVerifyFormQueue, Exchange sagaExchange) {
		return BindingBuilder.bind(ordersVerifyFormQueue).to(sagaExchange)
			.with("orders.form.verify.routing.key").noargs();
	}

	@Bean
	public Binding decrementPointBinding() {
		return BindingBuilder.bind(ordersDecrementPointQueue()).to(sagaExchange())
			.with("point.decrement.routing.key").noargs();
	}

	@Bean
	public Binding applyCouponBinding() {
		return BindingBuilder.bind(ordersApplyCouponQueue()).to(sagaExchange())
			.with("coupon.apply.routing.key").noargs();
	}

	@Bean
	public Binding approvePaymentBinding() {
		return BindingBuilder.bind(ordersPaymentApproveQueue()).to(sagaExchange())
			.with("orders.approve.payment.routing.key").noargs();
	}

	@Bean
	public Binding saveOrdersDatabaseBinding() {
		return BindingBuilder.bind(ordersSaveOrderDatabaseQueue()).to(sagaExchange())
			.with("orders.save.database.routing.key").noargs();
	}

	@Bean
	public Binding earnPointBinding() {
		return BindingBuilder.bind(pointEarnQueue()).to(sagaExchange())
			.with("point.earn.routing.key").noargs();
	}

	@Bean
	public Binding deleteCartBinding() {
		return BindingBuilder.bind(cartDeleteQueue()).to(sagaExchange())
			.with("cart.delete.routing.key").noargs();
	}

	@Bean
	public Binding cancelPaymentBinding() {
		return BindingBuilder.bind(paymentCancelQueue()).to(sagaExchange())
			.with("payment.cancel.routing.key").noargs();
	}

	@Bean
	public Binding pointRequestPayCancelBinding() {
		return BindingBuilder.bind(pointRequestPayAmountQueue()).to(sagaExchange())
			.with("point.request.pay.cancel.routing.key").noargs();
	}

	@Bean
	public Binding ordersRequestPayCancelBinding() {
		return BindingBuilder.bind(ordersRequestPayCancelQueue()).to(sagaExchange())
			.with("orders.request.pay.cancel.routing.key").noargs();
	}

	@Bean
	public Binding requestPayCancelBinding() {
		return BindingBuilder.bind(requestPayCancelQueue())
			.to(sagaExchange())
			.with("pay.cancel.routing.key")
			.noargs();
	}


	// dead queue
	@Bean
	public Binding deadOrdersSagaBinding() {
		return BindingBuilder.bind(deadOrdersSagaQueue()).to(sagaExchange())
			.with("nova.orders.saga.dead.routing.key").noargs();
	}

	// 보상트랜잭션 바인딩
	@Bean
	public Binding compensateOrdersConfirmBinding() {
		return BindingBuilder.bind(compensateOrdersConfirmFormQueue()).to(sagaExchange())
			.with("compensate.orders.form.confirm.routing.key").noargs();
	}

	@Bean
	public Binding compensateDecrementPointBinding() {
		return BindingBuilder.bind(compensateOrdersDecrementPointQueue()).to(sagaExchange())
			.with("compensate.point.decrement.routing.key").noargs();
	}

	@Bean
	public Binding compensateApplyCouponBinding() {
		return BindingBuilder.bind(compensateOrdersApplyCouponQueue()).to(sagaExchange())
			.with("compensate.coupon.apply.routing.key").noargs();
	}

	@Bean
	public Binding compensateApprovePaymentBinding() {
		return BindingBuilder.bind(compensateApprovePaymentQueue()).to(sagaExchange())
			.with("compensate.approve.payment.routing.key").noargs();
	}

	@Bean
	public RabbitTemplate ordersRabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(converter());
		return rabbitTemplate;
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(converter());
		return factory;
	}

	// SAGA BINDING
	@Bean
	public Binding api1ProducerBinding() {
		return BindingBuilder.bind(api1ProducerQueue())
			.to(sagaExchange())
			.with("nova.api1-producer-routing-key")
			.noargs();
	}

	@Bean
	public Binding api2ProducerBinding() {
		return BindingBuilder.bind(api2ProducerQueue())
			.to(sagaExchange())
			.with("nova.api2-producer-routing-key")
			.noargs();
	}

	@Bean
	public Binding api3ProducerBinding() {
		return BindingBuilder.bind(api3ProducerQueue())
			.to(sagaExchange())
			.with("nova.api3-producer-routing-key")
			.noargs();
	}

	@Bean
	public Binding api4ProducerBinding() {
		return BindingBuilder.bind(api4ProducerQueue())
			.to(sagaExchange())
			.with("nova.api4-producer-routing-key")
			.noargs();
	}

	@Bean
	public Binding api5ProducerBinding() {
		return BindingBuilder.bind(api5ProducerQueue())
			.to(sagaExchange())
			.with("nova.api5-producer-routing-key")
			.noargs();
	}

	@Bean
	public Binding api6ProducerBinding() {
		return BindingBuilder.bind(api6ProducerQueue())
			.to(sagaExchange())
			.with("nova.api6-producer-routing-key")
			.noargs();
	}


}
