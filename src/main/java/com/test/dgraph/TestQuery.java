package com.test.dgraph;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import io.dgraph.DgraphAsyncClient;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.Transaction;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto.Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TestQuery {

	public static void main(String[] args) {
		// 链接地址
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9080).usePlaintext(true).build();
		DgraphStub stub = DgraphGrpc.newStub(channel);
		// DgraphClient dgraphClient = new DgraphClient(Collections.singletonList(stub));
		DgraphClient dgraphClient = new DgraphClient(stub); 
		
		/**
		 * 一次添加索引,一次数据突变后,即可自己查询
		 * query
		 */
		
		// Query
		Gson gson = new Gson();
		String query =
		"query all($a: string){\n" +
		"  all(func: eq(name, $a)) {\n" +
		"    name\n" +
		"  }\n" +
		"}\n";
		Map<String, String> vars = Collections.singletonMap("$a", "53001");
		long time1 = System.currentTimeMillis();
		System.out.println("begin:"+time1);
		Response res = dgraphClient.newReadOnlyTransaction().queryWithVars(query, vars);
		//Response res = dgraphClient.newTransaction().queryWithVars(query, vars);
		long time2 = System.currentTimeMillis();
		System.out.println("end:"+time2);
		System.out.println("time:"+(time2-time1));
		
		/**
		 * 在大多数情况下，使用正常的读写事务，它可以有任意数量的查询或变异操作。但是，如果一个事务只有查询，那么您可能会受益于只读事务，它可以在多个这样的只读事务中共享相同的读取时间戳，并且可以
		 * 导致较低的延迟。
		 * In most of the cases, the normal read-write transactions is used, which can have any number of query or mutate operations. 
		 * However, if a transaction only has queries, you might benefit from a read-only transaction, which can share the same read
		 *  timestamp across multiple such read-only transactions and can result in lower latencies.
		 */
		Transaction txn = dgraphClient.newReadOnlyTransaction();
		long begin1 = System.currentTimeMillis();
		Map<String, String> vars2 = Collections.singletonMap("$a", "233221");
		Response res2 =txn .queryWithVars(query, vars2);
		long begin2 = System.currentTimeMillis();
		System.out.println("begin2:"+(begin2-begin1));
		Map<String, String> vars3 = Collections.singletonMap("$a", "1");
		Response res3 =txn .queryWithVars(query, vars3);
		long begin3 = System.currentTimeMillis();
		System.out.println("begin2:"+(begin3-begin2));
	
		// Deserialize
		People ppl = gson.fromJson(res.getJson().toStringUtf8(), People.class);

		// Print results
		System.out.printf("people found: %d\n", ppl.all.size());

		ppl.all.forEach(person -> System.out.println(person.name));

	}
	class Person {
		String name;
		Person() {
		}
	}

	class People {
		  List<Person> all;
		  People() {}
		}

}

