package io.nebula.market.account.infrastructure.adapter.input.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.nebula.market.account.application.dto.request.CreateAccountRequest;
import io.nebula.market.account.application.dto.request.UpdateAccountRequest;
import io.nebula.market.account.application.dto.response.ReadAccountResponse;
import io.nebula.market.account.application.dto.response.ReadAccountsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "계정 API", description = "계정 관리를 위한 API")
public interface AccountApiDocs {

    @Operation(
            summary = "새 계정 생성",
            description = "새로운 계정을 생성합니다.",
            requestBody = @RequestBody(
                    description = "생성할 계정 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateAccountRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "계정이 성공적으로 생성되었습니다.",
                            headers = @Header(name = "Location", description = "생성된 계정의 URI")
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<String> createAccount(CreateAccountRequest request);

    @Operation(
            summary = "기존 계정 업데이트",
            description = "기존 계정을 업데이트합니다.",
            requestBody = @RequestBody(
                    description = "업데이트할 계정 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateAccountRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "계정이 성공적으로 업데이트되었습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "계정을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<Void> updateAccount(UUID id, UpdateAccountRequest request);

    @Operation(
            summary = "계정 삭제",
            description = "기존 계정을 삭제합니다.",
            parameters = @Parameter(
                    name = "id",
                    description = "삭제할 계정의 ID",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "계정이 성공적으로 삭제되었습니다."),
                    @ApiResponse(responseCode = "404", description = "계정을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<Void> deleteAccount(UUID id);

    @Operation(
            summary = "모든 계정 목록 조회",
            description = "모든 계정의 목록을 페이지네이션하여 조회합니다.",
            parameters = @Parameter(
                    name = "pageable",
                    description = "페이지네이션 정보",
                    required = false,
                    schema = @Schema(implementation = Pageable.class)
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "계정 목록 조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = ReadAccountsResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<Page<ReadAccountsResponse>> getAccounts(Pageable pageable);

    @Operation(
            summary = "ID로 계정 세부 정보 조회",
            description = "ID를 사용하여 특정 계정의 세부 정보를 조회합니다.",
            parameters = @Parameter(
                    name = "id",
                    description = "조회할 계정의 ID",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "계정 세부 정보 조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = ReadAccountResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "계정을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<ReadAccountResponse> getAccountById(UUID id);
}