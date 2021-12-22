import pymysql as pms


def input_date():
    while True:

        input_data = input('날짜 입력 예) 2021년 5월 7일 -> 20210507 : ')
        input_len = len(input_data)

        if input_len != 8:
            print('총 길이가 8자리여야 합니다. 다시 입력해주세요.')
            continue

        else:

            error_flag = False
            for i in range(0, 8):
                target = int(input_data[i])
                if not ((0 <= target) and (target <= 9)):
                    error_flag = True
                    break

            if error_flag:
                print('정상적인 숫자 입력이 아닙니다. 다시 입력해주세요.')
                continue
            else:
                return input_data


def input_balance():

    while True:

        input_data = input('금액 입력 : ')
        balance_len = len(input_data)

        error_flag = False
        for i in range(0, balance_len):
            if not ('0' <= input_data[i] <= '9'):
                error_flag = True
                break

        if error_flag:
            print('정상적인 숫자 입력이 아닙니다. 다시 입력해주세요.')
            continue
        else:
            return input_data


connection = pms.connect(
    host='localhost',
    port=3306,
    user='root',
    password='****',
    db='bank',
    charset='utf8'
)

cursor = connection.cursor()

try:

    exit_flag = False
    while True:
        print('0. 종료')
        print('1. 매니저 메뉴')
        print('2. 사용자 메뉴')
        first_menu = input("선택할 메뉴를 입력해주세요: ")

        # 종료
        if int(first_menu) == 0:
            print("이용해주셔서 감사합니다.")
            exit_flag = True
            break

        # 매니저 메뉴
        elif int(first_menu) == 1:

            # 등록된 관리자가 있는지 조회
            cursor.execute('SELECT * FROM manager')
            mgr_count = cursor.rowcount

            # 등록된 관리자 없음 -> 새로운 관리자 정보 등록
            if mgr_count <= 0:

                print('등록된 관리자 정보가 없습니다. 새 관리자 정보를 등록해주세요.')

                input_ssn = input("관리자 고유번호 6자리 입력: ")
                name = input("이름 입력: ")
                password = input("비밀번호 입력: ")

                sql = 'INSERT INTO manager (Ssn, Name, Password) VALUES (%s, %s, %s)'
                cursor.execute(sql, (input_ssn, name, password))
                connection.commit()

                print('관리자 정보가 성공적으로 등록되었습니다.')

            # 등록된 관리자 있음 -> 로그인
            else:

                while True:
                    input_ssn = input("관리자 고유번호 6자리 입력: ")
                    password = input("비밀번호 입력: ")

                    sql = 'SELECT * FROM manager WHERE Ssn=' + input_ssn + ' AND Password=' + password
                    cursor.execute(sql)
                    is_valid = cursor.rowcount

                    # 일치하는 정보 있음 -> 로그인 성공
                    if is_valid > 0:
                        break

                    # 일치하는 정보 없음 -> 로그인 실패로 다시 입력받기
                    else:
                        print('고유번호 또는 비밀번호가 잘못되었습니다. 다시 입력해주세요.')

            while True:
                print('0. 종료')
                print('1. 뒤로가기')
                print('2. 신규 사용자 등록')
                print('3. 사용자 계좌 관리')
                print('4. 사용자 입출금 내역 관리')
                print('5. 사용자 정보 수정')
                print('6. 사용자 정보 삭제')
                print('7. 사용자 정보 조회')
                print('8. 관리자 계정 생성')
                print('9. 관리자 정보 수정')
                print('10. 관리자 정보 삭제')
                print('11. 관리자 정보 조회')
                second_menu = input("선택할 메뉴를 입력해주세요: ")

                # 종료
                if int(second_menu) == 0:
                    print("이용해주셔서 감사합니다.")
                    exit_flag = True
                    break

                # 뒤로가기
                elif int(second_menu) == 1:
                    print("--------------------")
                    break

                # 신규 사용자 등록
                elif int(second_menu) == 2:

                    while True:

                        input_ssn = input("사용자 고유번호 6자리 입력: ")

                        # 중복 체크
                        sql = 'SELECT * FROM customer WHERE Ssn=' + input_ssn
                        cursor.execute(sql)
                        duplicated = cursor.rowcount

                        # 고유번호 중복
                        if duplicated > 0:
                            print('중복된 고유번호가 있습니다. 다시 입력해주세요.')
                            continue

                        name = input("이름 입력: ")
                        si_do = input("주소 시/도 입력: ")
                        si_gun_gu = input("주소 시/군/구 입력: ")
                        eup_myeon_dong = input("주소 읍/면/동 입력: ")
                        detailed_address = input("상세주소 입력: ")
                        phone_number = input('연락처 입력 예) 01012341234 : ')

                        # 관리자 고유번호(Mssn) 설정
                        cursor.execute('SELECT * FROM manager')
                        Mssn = cursor.fetchone()[0]

                        # 사용자 등록
                        sql = 'INSERT INTO customer (Ssn, Name, Si_do, Si_gun_gu, Eup_myeon_dong, Detailed_address, ' \
                              'Mssn) VALUES (%s, %s, %s, %s, %s, %s, %s)'
                        cursor.execute(sql, (input_ssn, name, si_do, si_gun_gu, eup_myeon_dong,
                                             detailed_address, Mssn))
                        connection.commit()

                        # 연락처 등록
                        cursor.execute('INSERT INTO phone_number (phone_number, Ssn) VALUES (%s, %s)',
                                       (phone_number, input_ssn))
                        connection.commit()

                        print('사용자 등록이 성공적으로 완료되었습니다.')
                        break

                # 사용자 계좌 관리
                elif int(second_menu) == 3:

                    while True:

                        print('0. 뒤로가기')
                        print('1. 계좌 생성')
                        print('2. 계좌 삭제')
                        third_menu = input("선택할 메뉴를 입력해주세요: ")

                        # 뒤로가기
                        if int(third_menu) == 0:
                            print("--------------------")
                            break

                        # 계좌 생성
                        elif int(third_menu) == 1:

                            while True:

                                # 고유번호 확인
                                customer_ssn = input("사용자 고유번호 6자리 입력: ")
                                cursor.execute('SELECT * FROM customer WHERE Ssn=' + customer_ssn)
                                customer_cnt = cursor.rowcount

                                # 일치하는 정보 없음
                                if customer_cnt <= 0:
                                    print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                                    continue

                                while True:

                                    # 계좌번호 중복 확인
                                    account_number = input('등록할 계좌번호 6자리 입력: ')
                                    cursor.execute('SELECT * FROM account WHERE Number=' + account_number)
                                    account_cnt = cursor.rowcount

                                    # 계좌번호 중복
                                    if account_cnt > 0:
                                        print('중복되는 계좌번호가 있습니다. 다시 입력해주세요.')
                                        continue

                                    date = input_date()
                                    password = input('비밀번호 입력: ')

                                    sql = 'INSERT INTO account (Number, Balance, Open_date, Password, Ssn) ' \
                                          'VALUES (%s, %s, %s, %s, %s)'
                                    cursor.execute(sql, (account_number, 0, date, password, customer_ssn))
                                    connection.commit()

                                    print('계좌 등록이 성공적으로 완료되었습니다.')
                                    break

                                break

                        # 계좌 삭제
                        elif int(third_menu) == 2:

                            while True:

                                # 고유번호 확인
                                customer_ssn = input("사용자 고유번호 6자리 입력: ")
                                cursor.execute('SELECT * FROM customer WHERE Ssn=' + customer_ssn)
                                customer_cnt = cursor.rowcount

                                # 일치하는 정보 없음
                                if customer_cnt <= 0:
                                    print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                                    continue

                                while True:

                                    # 계좌번호 중복 확인
                                    account_number = input('삭제할 계좌번호 6자리 입력: ')
                                    cursor.execute('SELECT * FROM account WHERE Number=' + account_number)
                                    account_cnt = cursor.rowcount

                                    # 계좌번호 없음
                                    if account_cnt <= 0:
                                        print('해당되는 계좌가 없습니다. 다시 입력해주세요.')
                                        continue

                                    password = input('비밀번호 입력: ')

                                    # 사용자 고유번호 foreign key 설정 해제
                                    cursor.execute('SET foreign_key_checks = 0')
                                    connection.commit()

                                    # 관련 입출금 내역 삭제
                                    cursor.execute('DELETE FROM history WHERE Anum=' + account_number)
                                    connection.commit()

                                    # 계좌 삭제
                                    cursor.execute(
                                        'DELETE FROM account WHERE Number=' + account_number + ' AND Password=' + password)
                                    connection.commit()

                                    # 사용자 고유번호 foreign key 설정
                                    cursor.execute('SET foreign_key_checks = 1')
                                    connection.commit()

                                    print('계좌 삭제가 성공적으로 완료되었습니다.')
                                    break

                                break

                        # 잘못된 입력
                        else:
                            print('잘못된 입력입니다. 다시 입력해주세요.')
                            continue

                        break

                # 사용자 입출금 내역 관리
                elif int(second_menu) == 4:

                    print('0. 뒤로가기')
                    print('1. 입출금 내역 조회')
                    print('2. 입출금 내역 수정')
                    print('3. 입출금 내역 삭제')
                    third_menu = input("선택할 메뉴를 입력해주세요: ")

                    # 뒤로가기
                    if int(third_menu) == 0:
                        print("--------------------")
                        continue

                    # 입출금 내역 조회
                    elif int(third_menu) == 1:

                        while True:

                            # 고유번호 확인
                            customer_ssn = input('사용자 고유번호 6자리 입력: ')
                            cursor.execute('SELECT * FROM customer WHERE Ssn=' + customer_ssn)
                            customer_cnt = cursor.rowcount

                            # 일치하는 정보 없음
                            if customer_cnt <= 0:
                                print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                                continue

                            # 조회할 계좌 확인
                            account_number = input('조회할 계좌번호 6자리 입력: ')
                            cursor.execute('SELECT * FROM account WHERE Number=' + account_number +
                                           ' AND Ssn=' + customer_ssn)
                            is_exist = cursor.rowcount

                            # 일치하는 계좌 없음
                            if is_exist <= 0:
                                print('사용자 정보와 일치하는 계좌가 존재하지 않습니다. 다시 입력해주세요.')
                                continue

                            sql = 'SELECT * FROM history WHERE Anum=' + account_number
                            cursor.execute(sql)
                            history_cnt = cursor.rowcount
                            resultset = cursor.fetchall()

                            # 입출금 내역 없음
                            if history_cnt <= 0:
                                print('입출금 내역이 없습니다.')

                            # 입출금 내역 출력
                            else:

                                print(
                                    '-------------------------------------------------------------------------------------'
                                    '------------------------------------')

                                for ret in resultset:
                                    year = ret[0][:4]
                                    month = ret[0][4:6]
                                    day = ret[0][6:]
                                    Date = year + '/' + month + '/' + day
                                    print('[' + ret[3] + '] 날짜:' + Date + ' 금액:' + str(ret[1]))

                                print(
                                    '-------------------------------------------------------------------------------------'
                                    '------------------------------------')

                            break

                    # 입출금 내역 수정
                    elif int(third_menu) == 2:

                        while True:

                            # 고유번호 확인
                            customer_ssn = input('사용자 고유번호 6자리 입력: ')
                            cursor.execute('SELECT * FROM customer WHERE Ssn=' + customer_ssn)
                            customer_cnt = cursor.rowcount

                            # 일치하는 정보 없음
                            if customer_cnt <= 0:
                                print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                                continue

                            # 조회할 계좌 확인
                            account_number = input('조회할 계좌번호 6자리 입력: ')
                            cursor.execute('SELECT * FROM account WHERE Number=' + account_number +
                                           ' AND Ssn=' + customer_ssn)
                            is_exist = cursor.rowcount

                            # 일치하는 계좌 없음
                            if is_exist <= 0:
                                print('사용자 정보와 일치하는 계좌가 존재하지 않습니다. 다시 입력해주세요.')
                                continue

                            # 날짜와 잔액을 입력받아 수정할 입출금 내역 선택
                            date = input_date()
                            balance = input_balance()

                            sql = 'SELECT * FROM history WHERE Anum=' + account_number + ' AND Date=' + date + \
                                  ' AND Amount=' + balance
                            cursor.execute(sql)
                            history_cnt = cursor.rowcount
                            target_history = cursor.fetchone()

                            # 입출금 내역 없음
                            if history_cnt <= 0:
                                print('해당 입출금 내역이 존재하지 않습니다.')

                            # 입출금 내역 수정
                            else:

                                print('입출금 내역 조회가 완료되었습니다.')

                                while True:

                                    print('어떤 내용을 수정하시겠습니까?')
                                    print('0. 뒤로가기')
                                    print('1. 날짜')
                                    print('2. 금액')
                                    print('3. 입출금 유형')
                                    fourth_menu = input('선택할 메뉴를 입력해주세요:')

                                    # 뒤로가기
                                    if int(fourth_menu) == 0:
                                        print("--------------------")
                                        break

                                    # 날짜
                                    elif int(fourth_menu) == 1:

                                        new_date = input_date()
                                        cursor.execute('UPDATE history SET Date = %s WHERE Anum = %s AND Date = %s '
                                                       'AND Amount = %s', (new_date, account_number, date, balance))
                                        connection.commit()

                                    # 금액
                                    elif int(fourth_menu) == 2:

                                        new_balance = input_balance()
                                        cursor.execute('UPDATE history SET Amount = %s WHERE Anum = %s AND Date = %s '
                                                       'AND Amount = %s', (new_balance, account_number, date, balance))
                                        connection.commit()

                                    # 입출금 유형
                                    elif int(fourth_menu) == 3:

                                        if target_history[3] == '입금':

                                            cursor.execute(
                                                'UPDATE history SET Type = %s WHERE Anum = %s AND Date = %s '
                                                'AND Amount = %s', ('출금', account_number, date, balance)
                                            )
                                            connection.commit()

                                        else:

                                            cursor.execute(
                                                'UPDATE history SET Type = %s WHERE Anum = %s AND Date = %s '
                                                'AND Amount = %s', ('입금', account_number, date, balance)
                                            )
                                            connection.commit()

                                    else:
                                        print('잘못된 입력입니다. 다시 입력해주세요.')
                                        continue

                                    print('입출금 내역이 성공적으로 수정되었습니다.')
                                    break

                            break

                    # 입출금 내역 삭제
                    elif int(third_menu) == 3:

                        while True:

                            # 고유번호 확인
                            customer_ssn = input('사용자 고유번호 6자리 입력: ')
                            cursor.execute('SELECT * FROM customer WHERE Ssn=' + customer_ssn)
                            customer_cnt = cursor.rowcount

                            # 일치하는 정보 없음
                            if customer_cnt <= 0:
                                print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                                continue

                            # 조회할 계좌 확인
                            account_number = input('조회할 계좌번호 6자리 입력: ')
                            cursor.execute('SELECT * FROM account WHERE Number=' + account_number +
                                           ' AND Ssn=' + customer_ssn)
                            is_exist = cursor.rowcount

                            # 일치하는 계좌 없음
                            if is_exist <= 0:
                                print('사용자 정보와 일치하는 계좌가 존재하지 않습니다. 다시 입력해주세요.')
                                continue

                            # 날짜와 잔액을 입력받아 수정할 입출금 내역 선택
                            date = input_date()
                            balance = input_balance()

                            sql = 'SELECT * FROM history WHERE Anum=' + account_number + ' AND Date=' + date + \
                                  ' AND Amount=' + balance
                            cursor.execute(sql)
                            history_cnt = cursor.rowcount
                            target_history = cursor.fetchone()

                            # 입출금 내역 없음
                            if history_cnt <= 0:
                                print('해당 입출금 내역이 존재하지 않습니다.')

                            # 입출금 내역 삭제
                            else:

                                cursor.execute('DELETE FROM history WHERE Anum=' + account_number + ' AND Date=' + date + \
                                  ' AND Amount=' + balance)
                                connection.commit()
                                print('입출금 내역이 성공적으로 삭제되었습니다.')

                            break

                    else:
                        print('잘못된 입력입니다. 다시 입력해주세요.')

                # 사용자 정보 수정
                elif int(second_menu) == 5:

                    while True:

                        # 고유번호 확인
                        customer_ssn = input('사용자 고유번호 6자리 입력: ')
                        cursor.execute('SELECT * FROM customer WHERE Ssn=' + customer_ssn)
                        customer_cnt = cursor.rowcount

                        # 일치하는 정보 없음
                        if customer_cnt <= 0:
                            print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                            continue

                        print('0. 뒤로가기')
                        print('1. 이름')
                        print('2. 주소')
                        print('3. 관리자 고유번호')
                        print('4. 연락처 추가')
                        print('5. 연락처 수정')
                        print('6. 연락처 삭제')
                        third_menu = input('수정할 정보 입력: ')

                        # 뒤로가기
                        if int(third_menu) == 0:
                            print("--------------------")
                            break

                        # 이름 수정
                        elif int(third_menu) == 1:
                            name = input('이름 입력: ')
                            cursor.execute('UPDATE customer SET Name = %s WHERE Ssn = %s', (name, customer_ssn))
                            connection.commit()
                            print('이름 수정이 성공적으로 완료되었습니다.')

                        # 주소 수정
                        elif int(third_menu) == 2:
                            si_do = input("주소 시/도 입력: ")
                            si_gun_gu = input("주소 시/군/구 입력: ")
                            eup_myeon_dong = input("주소 읍/면/동 입력: ")
                            detailed_address = input("상세주소 입력: ")
                            cursor.execute(
                                'UPDATE customer SET Si_do = %s, Si_gun_gu = %s, Eup_myeon_dong = %s, Detailed_address = %s '
                                'WHERE Ssn = %s', (si_do, si_gun_gu, eup_myeon_dong, detailed_address, customer_ssn))
                            connection.commit()
                            print('주소 수정이 성공적으로 완료되었습니다.')

                        # 관리자 고유번호 수정
                        elif int(third_menu) == 3:

                            while True:

                                mgr_ssn = input('관리자 고유번호 6자리 입력: ')
                                cursor.execute('SELECT * FROM manager WHERE Ssn=' + mgr_ssn)
                                mgr_count = cursor.rowcount

                                # 해당되는 관리자 없음
                                if mgr_count <= 0:
                                    print('해당되는 관리자가 존재하지 않습니다. 다시 입력해주세요.')
                                    continue

                                cursor.execute('UPDATE customer SET Mssn = %s WHERE Ssn = %s', (mgr_ssn, customer_ssn))
                                connection.commit()
                                print('관리자 고유번호 수정이 성공적으로 완료되었습니다.')

                                break

                        # 연락처 추가
                        elif int(third_menu) == 4:

                            phone_number = input('추가할 연락처 입력: ')
                            cursor.execute('INSERT INTO phone_number (phone_number, Ssn) VALUES (%s, %s)',
                                           (phone_number, customer_ssn))
                            connection.commit()

                        # 연락처 수정
                        elif int(third_menu) == 5:

                            # 연락처 목록 출력
                            print('해당 사용자의 연락처 목록은 다음과 같습니다.')
                            cursor.execute('SELECT * FROM phone_number WHERE Ssn=' + customer_ssn)
                            phone_numbers = cursor.fetchall()
                            for call in phone_numbers:
                                print(call[0])

                            while True:

                                current_number = input('어떤 연락처를 수정하시겠습니까?: ')
                                cursor.execute('SELECT * FROM phone_number WHERE phone_number=' + current_number)
                                is_exist = cursor.rowcount

                                # 해당되는 연락처 없음
                                if is_exist <= 0:
                                    print('해당되는 연락처가 없습니다. 다시 입력해주세요.')
                                    continue

                                update_number = input('등록할 번호 입력: ')
                                cursor.execute('UPDATE phone_number SET phone_number = %s WHERE phone_number = %s',
                                               (update_number, current_number))
                                connection.commit()

                                break

                        # 연락처 삭제
                        elif int(third_menu) == 6:

                            # 연락처 목록 출력
                            print('해당 사용자의 연락처 목록은 다음과 같습니다.')
                            cursor.execute('SELECT * FROM phone_number WHERE Ssn=' + customer_ssn)
                            phone_numbers = cursor.fetchall()
                            for call in phone_numbers:
                                print(call[0])

                            while True:

                                # 연락처 입력 받은 후 삭제
                                phone_number = input('삭제할 연락처 입력: ')
                                cursor.execute('SELECT * FROM phone_number WHERE phone_number=' + phone_number)
                                is_exist = cursor.rowcount

                                # 해당되는 연락처 없음
                                if is_exist <= 0:
                                    print('해당되는 연락처가 없습니다. 다시 입력해주세요.')
                                    continue

                                cursor.execute('DELETE FROM phone_number WHERE phone_number=' + phone_number)
                                connection.commit()

                                break

                        break

                # 사용자 정보 삭제
                elif int(second_menu) == 6:

                    while True:

                        # 고유번호 확인
                        customer_ssn = input('사용자 고유번호 6자리 입력: ')
                        cursor.execute('SELECT * FROM customer WHERE Ssn=' + customer_ssn)
                        customer_cnt = cursor.rowcount

                        # 일치하는 정보 없음
                        if customer_cnt <= 0:
                            print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                            continue

                        # 사용자 고유번호 foreign key 설정 해제
                        cursor.execute('SET foreign_key_checks = 0')
                        connection.commit()

                        # 관련 입출금 내역 삭제
                        cursor.execute('SELECT * FROM account WHERE Ssn=' + customer_ssn)
                        accounts = cursor.fetchall()

                        for account in accounts:
                            account_number = account[0]
                            cursor.execute('DELETE FROM history WHERE Anum=' + account_number)
                            connection.commit()

                        # 관련 계좌 삭제
                        cursor.execute('DELETE FROM account WHERE Ssn=' + customer_ssn)
                        connection.commit()

                        # 관련 연락처 삭제
                        cursor.execute('DELETE FROM phone_number WHERE Ssn=' + customer_ssn)
                        connection.commit()

                        # 사용자 정보 삭제
                        cursor.execute('DELETE FROM customer WHERE Ssn=' + customer_ssn)
                        connection.commit()

                        # 사용자 고유번호 foreign key 설정
                        cursor.execute('SET foreign_key_checks = 1')
                        connection.commit()

                        print('사용자 정보가 성공적으로 삭제되었습니다.')

                        break

                # 사용자 정보 조회
                elif int(second_menu) == 7:

                    cursor.execute('SELECT * FROM customer')
                    resultset = cursor.fetchall()
                    size = cursor.rowcount

                    # 사용자 정보 없음
                    if size <= 0:
                        print('등록된 사용자가 없습니다.')

                    # 사용자 정보 출력
                    else:

                        for ret in resultset:

                            Ssn = ret[0]
                            Name = ret[1]
                            Si_do = ret[2]
                            Si_gun_gu = ret[3]
                            Eup_myeon_dong = ret[4]
                            Detailed_address = ret[5]
                            Mssn = ret[6]

                            cursor.execute('SELECT * FROM phone_number WHERE Ssn=' + Ssn)
                            phone_number = cursor.fetchone()[0]

                            print(
                                '-------------------------------------------------------------------------------------'
                                '------------------------------------')
                            print('고유번호: ' + Ssn + ' | 이름: ' + Name + ' | 주소: ' + Si_do + ' ' + Si_gun_gu + ' ' +
                                  Eup_myeon_dong + ' ' + Detailed_address + ' | 관리자 고유번호: ' + Mssn + ' | 대표 전화번호: '
                                  + phone_number)

                            cursor.execute('SELECT * FROM account WHERE Ssn=' + Ssn)
                            accounts = cursor.fetchall()

                            account_cnt = 1
                            for account in accounts:
                                year = account[2][:4]
                                month = account[2][4:6]
                                day = account[2][6:]
                                Date = year + '/' + month + '/' + day

                                print('[' + str(account_cnt) + '번째 계좌] 계좌번호: ' + str(account[0]) + ' | 잔액: ' + str(
                                    account[1]) +
                                      ' | 계좌개설일: ' + Date + ' | 비밀번호: ' + str(account[3]))
                                account_cnt = account_cnt + 1

                        print(
                            '-------------------------------------------------------------------------------------'
                            '------------------------------------')

                # 관리자 계정 생성
                elif int(second_menu) == 8:

                    input_ssn = input("관리자 고유번호 6자리 입력: ")
                    name = input("이름 입력: ")
                    password = input("비밀번호 입력: ")

                    sql = 'INSERT INTO manager (Ssn, Name, Password) VALUES (%s, %s, %s)'
                    cursor.execute(sql, (input_ssn, name, password))
                    connection.commit()

                    print('관리자 정보가 성공적으로 등록되었습니다.')

                # 관리자 정보 수정
                elif int(second_menu) == 9:

                    while True:
                        mgr_ssn = input("관리자 고유번호 6자리 입력: ")
                        password = input("비밀번호 입력: ")
                        cursor.execute('SELECT * FROM manager WHERE Ssn=' + mgr_ssn + ' AND Password=' + password)
                        mgr_count = cursor.rowcount

                        # 로그인 실패
                        if mgr_count <= 0:
                            print('고유번호 또는 비밀번호가 잘못되었습니다. 다시 입력해주세요.')
                            continue

                        # 정보 수정
                        else:

                            while True:

                                print('0. 뒤로가기')
                                print('1. 이름')
                                print('2. 비밀번호')
                                third_menu = input('수정할 정보를 입력해주세요: ')

                                # 뒤로가기
                                if int(third_menu) == 0:
                                    print("--------------------")
                                    break

                                # 이름 수정
                                elif int(third_menu) == 1:
                                    name = input('원하는 이름 입력: ')
                                    cursor.execute('UPDATE manager SET Name = %s WHERE Ssn = %s', (name, mgr_ssn))
                                    connection.commit()
                                    print('이름이 성공적으로 등록되었습니다.')

                                # 비밀번호 수정
                                elif int(third_menu) == 2:
                                    password = input('원하는 비밀번호 입력: ')
                                    cursor.execute('UPDATE manager SET Password = %s WHERE Ssn = %s',
                                                   (password, mgr_ssn))
                                    connection.commit()
                                    print('비밀번호가 성공적으로 등록되었습니다.')

                                # 잘못된 입력
                                else:
                                    print("잘못된 입력입니다. 다시 입력해주세요.")
                                    continue

                                break

                        break

                # 관리자 계정 삭제
                elif int(second_menu) == 10:

                    while True:
                        mgr_ssn = input("삭제할 관리자 고유번호 6자리 입력: ")
                        password = input("비밀번호 입력: ")
                        cursor.execute('SELECT * FROM manager WHERE Ssn=' + mgr_ssn + ' AND Password=' + password)
                        mgr_count = cursor.rowcount

                        # 로그인 실패
                        if mgr_count <= 0:
                            print('고유번호 또는 비밀번호가 잘못되었습니다. 다시 입력해주세요.')
                            continue

                        # 계정 삭제
                        else:

                            # 사용자의 관리자 고유번호 foreign key 설정 해제
                            cursor.execute('SET foreign_key_checks = 0')
                            connection.commit()

                            cursor.execute('SELECT * FROM manager')
                            mgr_count = cursor.rowcount
                            fir_mgr = cursor.fetchone()

                            # 관리자가 최소 인원(1명)이면 삭제 금지
                            if mgr_count <= 1:
                                print('관리자는 최소 1명 있어야 합니다. 삭제를 종료합니다.')

                            # 해당 관리자 고유번호가 등록된 사용자 탐색 후 재설정
                            else:

                                while fir_mgr[0] == mgr_ssn:
                                    fir_mgr = cursor.fetchone()

                                cursor.execute('UPDATE customer SET Mssn = %s WHERE Mssn = %s', (fir_mgr[0], mgr_ssn))
                                connection.commit()

                                cursor.execute('DELETE FROM manager WHERE Ssn=' + mgr_ssn)
                                connection.commit()
                                print('성공적으로 삭제되었습니다.')

                            # 사용자의 관리자 고유번호 foreign key 설정
                            cursor.execute('SET foreign_key_checks = 1')
                            connection.commit()

                        break

                # 관리자 계정 조회
                elif int(second_menu) == 11:

                    cursor.execute('SELECT * FROM manager')
                    resultset = cursor.fetchall()

                    # 사용자 정보 출력
                    for ret in resultset:
                        Ssn = ret[0]
                        Name = ret[1]
                        Password = ret[2]

                        print('--------------------------------------------------------------')
                        print('고유번호: ' + Ssn + ' | 이름: ' + Name + ' | 비밀번호: ' + Password)

                    print('--------------------------------------------------------------')

                # 잘못된 입력
                else:
                    print("잘못된 입력입니다. 다시 입력해주세요.")

        # 사용자 메뉴
        elif int(first_menu) == 2:

            while True:

                print('0. 종료')
                print('1. 뒤로가기')
                print('2. 본인 계좌로 이체')
                print('3. 타인 계좌로 이체')
                second_menu = input("선택할 메뉴를 입력해주세요: ")

                # 종료
                if int(second_menu) == 0:
                    print("이용해주셔서 감사합니다.")
                    exit_flag = True
                    break

                # 뒤로가기
                elif int(second_menu) == 1:
                    print("--------------------")
                    break

                # 본인 계좌 이체
                elif int(second_menu) == 2:

                    while True:

                        user_ssn = input('사용자 고유번호 6자리 입력: ')

                        # 사용자 정보가 존재하는지 확인
                        cursor.execute('SELECT * FROM customer WHERE Ssn=' + user_ssn)
                        user_cnt = cursor.rowcount

                        # 해당되는 사용자 없음
                        if user_cnt <= 0:
                            print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                            continue

                        while True:

                            mode = input('출금은 0, 입금은 1, 뒤로가려면 -1을 입력해주세요: ')

                            # 뒤로가기
                            if int(mode) == -1:
                                print("--------------------")
                                break

                            # 출금
                            elif int(mode) == 0:

                                while True:

                                    from_account = input('출금할 계좌번호 6자리 입력: ')
                                    cursor.execute(
                                        'SELECT * FROM account WHERE Number=' + from_account + ' AND Ssn=' + user_ssn)
                                    from_account_exist = cursor.rowcount
                                    account = cursor.fetchone()

                                    # 존재하지 않음
                                    if from_account_exist <= 0:
                                        print('존재하지 않는 계좌번호입니다. 다시 입력해주세요.')
                                        continue

                                    # 비밀번호 확인
                                    account_password = input('비밀번호 입력: ')
                                    if account_password != account[3]:
                                        print('비밀번호가 일치하지 않습니다. 다시 입력해주세요.')
                                        continue

                                    amount = input('출금할 금액 입력: ')
                                    if int(amount) < 0:
                                        print('금액은 반드시 0 이상의 숫자여야 합니다. 다시 입력해주세요.')
                                        continue
                                    elif int(amount) > int(account[1]):
                                        print('잔액이 부족합니다. 다시 입력해주세요.')
                                        print('해당 계좌의 잔액은 ' + str(account[1]) + '원 입니다.')
                                        continue

                                    # 잔액 업데이트
                                    sql = 'UPDATE account SET Balance = %s WHERE Number = %s'
                                    cursor.execute(sql, (int(account[1]) - int(amount), from_account))
                                    connection.commit()

                                    date = input_date()

                                    # 출금 내역 입력
                                    sql = 'INSERT INTO history (Date, Amount, Anum, Type) VALUES (%s, %s, %s, %s)'
                                    cursor.execute(sql, (date, amount, from_account, '출금'))
                                    connection.commit()

                                    print('출금이 성공적으로 완료되었습니다.')

                                    break

                            # 입금
                            elif int(mode) == 1:

                                while True:

                                    from_account = input('입금할 계좌번호 6자리 입력: ')
                                    cursor.execute(
                                        'SELECT * FROM account WHERE Number=' + from_account + ' AND Ssn=' + user_ssn)
                                    from_account_exist = cursor.rowcount
                                    account = cursor.fetchone()

                                    # 존재하지 않음
                                    if from_account_exist <= 0:
                                        print('존재하지 않는 계좌번호입니다. 다시 입력해주세요.')
                                        continue

                                    # 비밀번호 확인
                                    account_password = input('비밀번호 입력: ')
                                    if account_password != account[3]:
                                        print('비밀번호가 일치하지 않습니다. 다시 입력해주세요.')
                                        continue

                                    amount = input('입금할 금액 입력: ')
                                    if int(amount) < 0:
                                        print('입금할 금액은 반드시 0 이상의 숫자여야 합니다. 다시 입력해주세요.')
                                        continue

                                    # 잔액 업데이트
                                    sql = 'UPDATE account SET Balance = %s WHERE Number = %s'
                                    cursor.execute(sql, (int(account[1]) + int(amount), from_account))
                                    connection.commit()

                                    date = input_date()

                                    # 입금 내역 입력
                                    sql = 'INSERT INTO history (Date, Amount, Anum, Type) VALUES (%s, %s, %s, %s)'
                                    cursor.execute(sql, (date, amount, from_account, '입금'))
                                    connection.commit()

                                    print('입금이 성공적으로 완료되었습니다.')

                                    break

                            # 잘못된 입력
                            else:
                                print("잘못된 입력입니다. 다시 입력해주세요.")
                                continue

                            break

                        break

                # 타인 계좌로 이체
                elif int(second_menu) == 3:

                    while True:

                        user_ssn = input('사용자 고유번호 6자리 입력: ')

                        cursor.execute('SELECT * FROM customer WHERE Ssn=' + user_ssn)
                        user_cnt = cursor.rowcount

                        # 해당되는 사용자 없음
                        if user_cnt <= 0:
                            print('일치하는 사용자 정보가 없습니다. 다시 입력해주세요.')
                            continue

                        to_account = input('입금 계좌번호 6자리 입력: ')

                        # 입금할 계좌가 존재하는지 확인
                        cursor.execute('SELECT * FROM account WHERE Number=' + to_account)
                        to_account_cnt = cursor.rowcount
                        to_account_info = cursor.fetchone()

                        # 해당되는 계좌 없음
                        if to_account_cnt <= 0:
                            print('일치하는 계좌가 없습니다. 다시 입력해주세요.')
                            continue

                        from_account = input('출금 계좌번호 6자리 입력: ')
                        cursor.execute(
                            'SELECT * FROM account WHERE Number=' + from_account + ' AND Ssn=' + user_ssn)
                        from_account_exist = cursor.rowcount
                        account = cursor.fetchone()

                        # 존재하지 않음
                        if from_account_exist <= 0:
                            print('존재하지 않는 계좌번호입니다. 다시 입력해주세요.')
                            continue

                        # 비밀번호 확인
                        account_password = input('비밀번호 입력: ')
                        if account_password != account[3]:
                            print('비밀번호가 일치하지 않습니다. 다시 입력해주세요.')
                            continue

                        # 해당되는 사용자 없음
                        if to_account_cnt <= 0:
                            print('일치하는 계좌가 없습니다. 다시 입력해주세요.')
                            continue

                        amount = input('출금할 금액 입력: ')
                        if int(amount) < 0:
                            print('금액은 반드시 0 이상의 숫자여야 합니다. 다시 입력해주세요.')
                            continue
                        elif int(amount) > int(account[1]):
                            print('잔액이 부족합니다. 다시 입력해주세요.')
                            print('해당 계좌의 잔액은 ' + str(account[1]) + '원 입니다.')
                            continue

                        # 잔액 업데이트
                        sql = 'UPDATE account SET Balance = %s WHERE Number = %s'
                        cursor.execute(sql, (int(account[1]) - int(amount), from_account))
                        connection.commit()

                        sql = 'UPDATE account SET Balance = %s WHERE Number = %s'
                        cursor.execute(sql, (int(to_account_info[1]) + int(amount), to_account))
                        connection.commit()

                        date = input_date()

                        # 출금 내역 입력
                        sql = 'INSERT INTO history (Date, Amount, Anum, Type) VALUES (%s, %s, %s, %s)'
                        cursor.execute(sql, (date, amount, from_account, '출금'))
                        connection.commit()

                        # 입금 내역 입력
                        sql = 'INSERT INTO history (Date, Amount, Anum, Type) VALUES (%s, %s, %s, %s)'
                        cursor.execute(sql, (date, amount, to_account, '입금'))
                        connection.commit()

                        break

                # 잘못된 입력
                else:
                    print("잘못된 입력입니다. 다시 입력해주세요.")

        # 잘못된 입력
        else:
            print("잘못된 입력입니다. 다시 입력해주세요.")

        # 종료 처리
        if exit_flag:
            break

finally:
    connection.close()
