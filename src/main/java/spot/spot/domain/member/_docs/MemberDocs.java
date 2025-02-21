package spot.spot.domain.member._docs;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spot.spot.domain.member.dto.response.TokenDTO;

@Tag(name = "member", description = "회원 공통 API")
public interface MemberDocs {

    @Operation(summary = "시킬 일 서버에 등록하기",
        description = """
        Oauth2 매번 하기가 번거로울 거 같아서 만들었습니다. DB에 있는 회원 중 한 명의 id를 입력하면 accessToken을 줍니다.
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
        })
    @GetMapping("/developer-get-token")
    public TokenDTO getToken4Developer(@RequestParam long id);

}
