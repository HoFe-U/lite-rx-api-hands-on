package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.BlockingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Learn how to call blocking code from Reactive one with adapted concurrency strategy for
 * blocking code that produces or receives data.
 *
 * For those who know RxJava:
 *  - RxJava subscribeOn = Reactor subscribeOn
 *  - RxJava observeOn = Reactor publishOn
 *  - RxJava Schedulers.io <==> Reactor Schedulers.elastic
 *
 * @author Sebastien Deleuze
 * @see Flux#subscribeOn(Scheduler)
 * @see Flux#publishOn(Scheduler)
 * @see Schedulers
 */
public class Part11BlockingToReactive {

//========================================================================================

	// TODO Create a Flux for reading all users from the blocking repository deferred until the flux is subscribed, and run it with a bounded elastic scheduler
	Flux<User> blockingRepositoryToFlux(BlockingRepository<User> repository) {
		return Flux.defer(()-> Flux.fromIterable(repository.findAll()))	// 바로 fromIterable 을 하면 repostiory 에서 미리 호출 되어 문제발생하여(deffer 로 subscribe 시점까지 보냄)
				   .subscribeOn(Schedulers.boundedElastic());
	}

//========================================================================================

	// TODO Insert users contained in the Flux parameter in the blocking repository using a bounded elastic scheduler and return a Mono<Void> that signal the end of the operation
	Mono<Void> fluxToBlockingRepository(Flux<User> flux, BlockingRepository<User> repository) {
	    return flux
	            .publishOn(Schedulers.boundedElastic()) // 블로킹 호출을 위한 bounded elastic 스케줄러 사용
	            .doOnNext(repository::save)            // Flux에서 전달된 각 User를 저장
	            .then();
	}

}
