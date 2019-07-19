package com.test.dgraph;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Operation;
import io.dgraph.DgraphProto.Response;
import io.dgraph.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClassStuInfo {

	public static void main(String[] args) {

		// 链接地址
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9080).usePlaintext(true).build();
		DgraphStub stub = DgraphGrpc.newStub(channel);
		DgraphClient dgraphClient = new DgraphClient(stub);

		//查询班级
		String query =
				"query all($a: string){\n"+
				 "me(func: eq(name,$a)) {\n"+
				    "name\n"+
				  "}\n"+
				"}\n";
		Map<String, String> vars = Collections.singletonMap("$a", "初一一班");
		Response res = dgraphClient.newReadOnlyTransaction().queryWithVars(query, vars);
		System.out.println(res.getJson().toStringUtf8());

	}

}
