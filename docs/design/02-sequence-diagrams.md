# Sequence Diagrams

## 1. 상품(Product) 도메인

### 1.1. 상품 목록 조회

```mermaid
sequenceDiagram
		participant U as User
		participant PC as ProductController
		participant BS as BrandService
		participant PS as ProductService	
		participant PR as ProductRepository
		
		U ->> PC: 상품 목록 조회
		PC ->> BS: 브랜드 정보 조회
		alt 브랜드 조회 실패
				BS -->> PC: 404 NOT FOUND
		else 브랜드 조회 성공
				BS ->> PC: 브랜드 목록
				PC ->> PS: 상품 목록 조회
				alt 페이징, 필터 유효성 검증 실패
						PS -->> PC: 400 BAD REQUEST
				else 페이징, 필터 유효성 검증 성공 
						PS ->> PR: 상품 엔티티 조회
						alt 조회 실패
								PR -->> PS: 500 INTERNAL SERVER ERROR
						else 조회 성공
								PR -->> PS: 상품 엔티티 반환
						end
				end
		end
```

### 1.2. 상품 정보 조회

```mermaid
sequenceDiagram
		participant U as User
		participant PC as ProductController
		participant PS as ProductService
		participant PR as ProductRepository
		
		U ->> PC: 상품 정보 조회(productId)
		PC ->> PS: 상품 정보 조회(productId)
		PS ->> PR: 상품 정보 조회(productId)
		PR -->> PS: 상품 정보 반환
		alt 미등록 상품
				PS ->> PC: 404 NOT FOUND
		else 등록 상품
				PS ->> PC: 상품 정보 반환
		end
```

## 2. 브랜드(Brand) 도메인

### 2.1. 브랜드 정보 조회

## 3. 좋아요

### 3.1. 상품 좋아요 등록

### 3.2. 상품 좋아요 취소

### 3.3. 내가 좋아요 한 상품 목록 조회

## 4. 주문(Order) 도메인

### 4.1. 주문 요청

```mermaid
sequenceDiagram
		participant U as User
		participant OC as OrderController
		participant OS as OrderService
		participant PRS as ProductService
		participant PTS as PointService
		
		U ->> OC: 주문 요청
		alt 미 로그인
				OC -->> U: 401 Unauthorized
		else 로그인
				OC ->> OS: 주문 요청
				OS ->> PRS: 재고 차감 요청
				alt 재고 없음/판매 중단된 상품
						PRS -->> OS: 409 Conflict
				else 존재하지 않는 상품
						PRS -->> OS: 404 Not Found
				else 재고 차감 성공
						OS ->> PTS: 포인트 차감 요청
						alt 포인트 부족
								PTS -->> OS: 409 Conflict
						else 포인트 차감 성공
								PTS -->> OS: 포인트 보유 내역
								OS -->> OC: 200 OK
						end
				end 
		end
		
```

### 4.2. 주문 목록 조회

### 4.3. 단일 주문 상세 조회
