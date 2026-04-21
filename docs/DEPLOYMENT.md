# EC2 배포 가이드

## 전체 흐름

```
GitHub push (main)
      ↓
GitHub Actions: 테스트 실행
      ↓
Docker 이미지 빌드 → GHCR 푸시
      ↓
EC2에 SSH → 이미지 pull → 컨테이너 재시작
```

이미지 레지스트리는 **GHCR (GitHub Container Registry)** 사용. GitHub 계정에 통합되어 별도 회원가입 불필요.

---

## 사전 준비

### 1. EC2 인스턴스 준비

EC2 접속 후:

```bash
# Docker 설치 (Ubuntu 기준)
sudo apt update
sudo apt install -y docker.io
sudo usermod -aG docker $USER
# 로그아웃 후 재접속

# PostgreSQL 설치 (Docker로)
docker run -d \
  --name postgres \
  --restart unless-stopped \
  -e POSTGRES_USER=ember \
  -e POSTGRES_PASSWORD=your-strong-password \
  -e POSTGRES_DB=ember \
  -p 5432:5432 \
  postgres:16

# EC2 보안 그룹: 8080 포트 인바운드 허용
```

### 2. SSH 키 준비

EC2 접속용 `.pem` 파일 내용 전체를 GitHub Secret에 등록할 예정.

---

## GitHub Secrets 설정

Repository → **Settings → Secrets and variables → Actions → New repository secret**

| Secret 이름 | 값 예시 |
|-------------|---------|
| `EC2_HOST` | `ec2-xx-xx-xx-xx.compute.amazonaws.com` |
| `EC2_USER` | `ubuntu` (또는 `ec2-user`) |
| `EC2_SSH_KEY` | `.pem` 파일 전체 내용 (BEGIN~END 포함) |
| `DB_URL` | `jdbc:postgresql://host.docker.internal:5432/ember` |
| `DB_USERNAME` | `ember` |
| `DB_PASSWORD` | 강력한 비밀번호 |
| `DDL_AUTO` | `update` (운영 시 `validate` 권장) |
| `JWT_SECRET` | 32자 이상 랜덤 문자열 |
| `GOOGLE_CLIENT_ID` | Google OAuth2 클라이언트 ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 시크릿 |
| `KAKAO_CLIENT_ID` | 카카오 REST API 키 |
| `KAKAO_CLIENT_SECRET` | 카카오 시크릿 |
| `FRONTEND_URL` | `https://for-youth.site` |

> `GITHUB_TOKEN`은 GitHub Actions가 자동 주입하므로 별도 등록 불필요.

> **DB_URL 주의**: 지금 배포 스크립트는 `-p 8080:8080`만 사용. 호스트의 PostgreSQL에 접근하려면 Docker 네트워크 구성이 필요. 가장 안정적인 방법은 **아래 "권장 구성: Docker Compose"** 참고.

---

## 권장 구성: Docker Compose로 DB+앱 같이 띄우기

EC2에 `/home/ubuntu/ember/docker-compose.yml` 생성:

```yaml
services:
  postgres:
    image: postgres:16
    restart: unless-stopped
    environment:
      POSTGRES_USER: ember
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: ember
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - ember-net

  api:
    image: ghcr.io/{GITHUB_USERNAME}/{REPO_NAME}:latest
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/ember
      DB_USERNAME: ember
      DB_PASSWORD: ${DB_PASSWORD}
      DDL_AUTO: update
      JWT_SECRET: ${JWT_SECRET}
      GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
      GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
      KAKAO_CLIENT_ID: ${KAKAO_CLIENT_ID}
      KAKAO_CLIENT_SECRET: ${KAKAO_CLIENT_SECRET}
      FRONTEND_URL: ${FRONTEND_URL}
    depends_on:
      - postgres
    networks:
      - ember-net

networks:
  ember-net:

volumes:
  postgres-data:
```

이 경우 `deploy.yml`의 `docker run` 부분을 다음으로 교체:

```bash
cd /home/ubuntu/ember
docker compose pull api
docker compose up -d
```

---

## GHCR 이미지 공개 설정 (첫 배포 후 1회)

첫 배포 후 이미지를 Public으로 전환해야 EC2가 인증 없이 pull 가능합니다.

1. `https://github.com/{GITHUB_USERNAME}?tab=packages` 접속
2. `ember-api` 패키지 클릭
3. 오른쪽 **Package settings**
4. 맨 아래 **Danger Zone → Change visibility → Public** 선택
5. 패키지 이름 입력 후 확인

> 이 과정을 건너뛰면 EC2에서 `docker pull` 실패 (`unauthorized`).
> Private 유지하려면 EC2에 PAT(`read:packages`)를 설정하고 `deploy.yml`에 로그인 단계 추가 필요.

---

## 배포 테스트

1. `main` 브랜치에 push → GitHub Actions 자동 실행
2. Actions 탭에서 진행 상황 확인
3. 배포 완료 후 EC2에서:
   ```bash
   docker ps
   docker logs ember-api
   curl http://localhost:8080/api/applications  # 401 응답이면 정상
   ```

---

## OAuth2 리다이렉트 URI 업데이트

EC2 배포 후 Google/Kakao 개발자 센터에서 리다이렉트 URI 추가:

- Google: `http://{EC2_DOMAIN}:8080/login/oauth2/code/google`
- Kakao: `http://{EC2_DOMAIN}:8080/login/oauth2/code/kakao`

> HTTPS 설정하면 도메인 뒤에 포트 빼고 `/login/oauth2/code/...`만 사용.

---

## 롤백

특정 커밋 이미지로 되돌리기:

```bash
docker pull ghcr.io/{GITHUB_USERNAME}/{REPO_NAME}:{COMMIT_SHA}
docker stop ember-api
docker rm ember-api
docker run -d --name ember-api ... ghcr.io/{GITHUB_USERNAME}/{REPO_NAME}:{COMMIT_SHA}
```

GitHub Actions가 매 커밋마다 SHA 태그를 남김.
