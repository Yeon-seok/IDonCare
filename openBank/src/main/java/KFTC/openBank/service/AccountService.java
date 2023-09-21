package KFTC.openBank.service;


import KFTC.openBank.domain.Bank;
import KFTC.openBank.domain.BankAccount;
import KFTC.openBank.domain.TransactionHistory;
import KFTC.openBank.domain.Type;
import KFTC.openBank.dto.*;
import KFTC.openBank.exception.AccountException;
import KFTC.openBank.exception.BackAccountException;
import KFTC.openBank.exception.TransactionHistoryException;
import KFTC.openBank.repository.AccountRepository;
import KFTC.openBank.repository.BankAccountRepository;
import KFTC.openBank.repository.BankRepository;
import KFTC.openBank.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.sql.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    //플랫폼에 등록된 계좌에서 잔액 조회
    @Transactional(readOnly = true)
    public BalanceResponseDto findBalance(String pinNumber) throws AccountException {
        Tuple result = accountRepository.findBankAndAccountNumberById(pinNumber);
        if(result==null){
           throw new AccountException.FintechNumNotFoundException("요청 하신 핀테크 이용 번호는 등록되어 있지 않습니다.");
        }
        //은행 코드 및 계좌 번호
        String bankId = (String) result.get(0);
        String accountNumber = (String) result.get(1);
        //은행 이름
        String bankName = bankRepository.findNameById(bankId);
        Long money = bankAccountRepository.findMoneyByIdAndBankId(accountNumber, bankId);
        return new BalanceResponseDto(bankName,money);
    }
    
    //계좌 실명 조회
    @Transactional(readOnly = true)
    public InquiryResponseDto findRealName(InquiryRequestDto inquiryRequestDto) throws AccountException {
        String accountName = bankAccountRepository.findNameById(inquiryRequestDto.getAccountNum(), inquiryRequestDto.getBankCodeStd(), inquiryRequestDto.getAccountHolderInfo());
        if(accountName == null){
            throw new AccountException.AccoutNotFoundException("요청 하신 정보와 일치하는 계좌는 없습니다.");
        }
        return new InquiryResponseDto(accountName);
    }

    //거래 내역 조회
    public TransactionResponseDto findTransactionList(TransactionRequestDto transactionRequestDto) throws TransactionHistoryException{
        System.out.println(transactionRequestDto.getFintechUseNum());
        Tuple result = accountRepository.findBankAndAccountNumberById(transactionRequestDto.getFintechUseNum());
        if(result==null){
            throw new AccountException.FintechNumNotFoundException("요청 하신 핀테크 이용 번호는 등록되어 있지 않습니다.");
        }
        //은행 코드 및 계좌 번호
        String bankId = (String) result.get(0);
        String accountNumber = (String) result.get(1);
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        if(transactionRequestDto.getInquiryType().equals("A") || transactionRequestDto.getInquiryType().equals("a")){
            transactionHistories = transactionHistoryRepository.findByDateAll(transactionRequestDto.getFromDate(), transactionRequestDto.getToDate(), accountNumber);
        }
        else if(transactionRequestDto.getInquiryType().equals("I") || transactionRequestDto.getInquiryType().equals("i")){
            transactionHistories = transactionHistoryRepository.findByCondition(transactionRequestDto.getFromDate(), transactionRequestDto.getToDate(), accountNumber, Type.DEPOSIT);
        }
        else if(transactionRequestDto.getInquiryType().equals("O") || transactionRequestDto.getInquiryType().equals("o")){
            transactionHistories = transactionHistoryRepository.findByCondition(transactionRequestDto.getFromDate(), transactionRequestDto.getToDate(), accountNumber, Type.WITHDRAWAL);
        }
        else{
            throw new TransactionHistoryException("올바른 inquiryType을 입력해주세요.");
        }
        List<ResList> resLists = new ArrayList<>();
        if(transactionHistories.size() == 0){
            throw new TransactionHistoryException("조회 결과가 없습니다.");
        }
        for (TransactionHistory trans : transactionHistories) {
            String formattedDate1 = trans.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String formattedDate2 = trans.getLocalDateTime().format(DateTimeFormatter.ofPattern("HHmmss"));
            resLists.add(new ResList(formattedDate1, formattedDate2, trans.getType().toString(), trans.getContent(), trans.getAmount(), trans.getBalance()));
        }
        return  new TransactionResponseDto("A0000", resLists);
    }


    //입금 이체
    @Transactional(rollbackFor = {AccountException.class, BackAccountException.class})
    public DepositResponseDto depositLogic(DepositRequestDto depositRequestDto) throws AccountException, BackAccountException {
        String depositName = bankAccountRepository.findNameByIdAndBankId(depositRequestDto.getCntrAccountNum(), depositRequestDto.getCntrAccountBankCodeStd());
        if(depositName==null){
            throw new AccountException.FintechNumNotFoundException("요청 하신 핀테크 기업의 계좌 번호는 없는 계좌 번호 입니다..");
        }
        //핀테크 기업의 은행 코드 및 계좌 번호
        String bankId = depositRequestDto.getCntrAccountBankCodeStd();
        String accountNumber = depositRequestDto.getCntrAccountNum();
        String bankName = bankRepository.findNameById(bankId);
        //최종 입금 하고자 하는 계좌의 소유자
        String FinalDepositName = bankAccountRepository.findNameByIdAndBankId(depositRequestDto.getRecvClientAccountNum(), depositRequestDto.getRecvClientBankCode());
        if(FinalDepositName.equals(null)){
            throw new AccountException.AccoutNotFoundException("최종 입금을 원하는 계좌는 없는 계좌입니다.");
        }
        //출금 계좌와 은행 id로 잔액 조회.
        Long money = bankAccountRepository.findMoneyByIdAndBankId(accountNumber, bankId);
        if(depositRequestDto.getTran_amt() > money) {
            throw new AccountException.AccoutInsufficientException("핀테크 계좌의 잔액이 부족합니다.");
        }
        if(depositRequestDto.getTran_amt() <= 0) {
            throw new AccountException.AccoutInsufficientException("최소 1원 이상 이체가 가능합니다.");
        }
        String record = depositRequestDto.getReqClientName() + "님으로 부터 받은 돈을 " + depositRequestDto.getRecvClientName() +"에게 " + depositRequestDto.getRecvDpsPrintContent() + "을(를) 이유로 입금 이체";
        PaymentDto payment = new PaymentDto(depositName, bankId, bankName, record, accountNumber, FinalDepositName, depositRequestDto.getCntrAccountBankCodeStd(), bankRepository.findNameById(depositRequestDto.getCntrAccountBankCodeStd()), depositRequestDto.getRecvDpsPrintContent(), depositRequestDto.getRecvClientAccountNum(), depositRequestDto.getTran_amt());
        withdraw(payment);
        deposit(payment);
        return new DepositResponseDto("A0000");
    }


    //출금 이체
    @Transactional(rollbackFor = {AccountException.class, BackAccountException.class})
    public WithdrawReponseDto withdrawLogic(WithdrawRequestDto withdrawRequestDto) throws AccountException, BackAccountException {
        Tuple result = accountRepository.findBankAndAccountNumberById(withdrawRequestDto.getFintechUseNum());
        if(result==null){
            throw new AccountException.FintechNumNotFoundException("요청 하신 핀테크 이용 번호는 등록되어 있지 않습니다.");
        }
        //출금자의 은행 코드 및 계좌 번호
        String bankId = (String) result.get(0);
        String accountNumber = (String) result.get(1);
        String bankName = bankRepository.findNameById(bankId);
        //핀테크 기업의 계좌가 유효한지.
        String depositName = bankAccountRepository.findNameByIdAndBankId(withdrawRequestDto.getCntrAccountNum(), withdrawRequestDto.getCntrAccountBankCodeStd());
        if(depositName.equals(null)){
            throw new AccountException.AccoutNotFoundException("핀테크 기업의 계좌가 일치하지 않습니다.");
        }
        //최종 입금 하고자 하는 계좌의 소유자
        String finalDepositName = bankAccountRepository.findNameByIdAndBankId(withdrawRequestDto.getRecvClientAccountNum(), withdrawRequestDto.getRecvClientBankCode());
        if(finalDepositName.equals(null)){
            throw new AccountException.AccoutNotFoundException("최종 입금을 원하는 계좌는 없는 계좌입니다.");
        }
        //최종 입금 하고자 하는 계좌의 유효성 체크
        BankAccount byNameAndBankIdAndId = bankAccountRepository.findByNameAndBankIdAndId(finalDepositName, withdrawRequestDto.getRecvClientBankCode(), withdrawRequestDto.getRecvClientAccountNum());
        if(!byNameAndBankIdAndId.getName().equals(withdrawRequestDto.getRecvClientName())){
            throw new AccountException.AccoutNotFoundException("최종 입금을 원하는 계좌의 소유자와 계좌 번호가 일치하지 않습니다.");
        }
        //출금 계좌와 은행 id로 잔액 조회.
        Long money = bankAccountRepository.findMoneyByIdAndBankId(accountNumber, bankId);
        if(withdrawRequestDto.getTran_amt() > money) {
            throw new AccountException.AccoutInsufficientException("출금 계좌의 잔액이 부족합니다.");
        }
        if(withdrawRequestDto.getTran_amt() <= 0) {
            throw new AccountException.AccoutInsufficientException("최소 1원 이상 이체가 가능합니다.");
        }
        String record = withdrawRequestDto.getReqClientName() + "님이 " + withdrawRequestDto.getRecvClientName() +"에게 " + withdrawRequestDto.getRecvDpsPrintContent() + "을 이유로 출금이체 요청";
        PaymentDto payment = new PaymentDto(withdrawRequestDto.getReqClientName(), bankId, bankName, withdrawRequestDto.getWdPrintContent(), accountNumber, depositName, withdrawRequestDto.getCntrAccountBankCodeStd(), bankRepository.findNameById(withdrawRequestDto.getCntrAccountBankCodeStd()), record, withdrawRequestDto.getCntrAccountNum(), withdrawRequestDto.getTran_amt());
        System.out.println(payment.toString());
        withdraw(payment);
        deposit(payment);
        return new WithdrawReponseDto("A0000", withdrawRequestDto.getReqClientName(), withdrawRequestDto.getRecvClientName(), withdrawRequestDto.getRecvClientBankCode(), withdrawRequestDto.getRecvClientAccountNum(), withdrawRequestDto.getRecvDpsPrintContent(), withdrawRequestDto.getTran_amt());
    }

    //출금
    public void withdraw(PaymentDto paymentDto) throws BackAccountException{
        System.out.println(paymentDto.toString());
        BankAccount account = bankAccountRepository.findByNameAndBankIdAndId(paymentDto.getWithdrawer(), paymentDto.getWithdrawerBankId(), paymentDto.getWithdrawerAccountNum());
        if(account == null){
            throw new BackAccountException.WithdrawException("출금하려는 계좌의 정보가 없습니다.");
        }
        try{
            BankAccount bankAccount = bankAccountRepository.findById(paymentDto.getWithdrawerAccountNum()).get();
            Long money = account.withdraw(paymentDto.getMoney());
            TransactionHistory transactionHistory = new TransactionHistory(bankAccount, paymentDto.getWithdrawerContent(), LocalDateTime.now(), paymentDto.getMoney(), Type.WITHDRAWAL, money);
            transactionHistoryRepository.save(transactionHistory);
        }catch (Exception e){
            throw new BackAccountException.WithdrawException("출금 중 문제가 발생했습니다!");
        }
    }

    //입금
    public void deposit(PaymentDto paymentDto) throws BackAccountException {
        System.out.println(paymentDto.toString());
        BankAccount account = bankAccountRepository.findByNameAndBankIdAndId(paymentDto.getDepositor(), paymentDto.getDepositorBankId(), paymentDto.getDepositorAccountNum());
        if(account == null){
            throw new BackAccountException.WithdrawException("입금하려는 계좌의 정보가 없습니다.");
        }
        try{
            BankAccount bankAccount = bankAccountRepository.findById(paymentDto.getDepositorAccountNum()).get();
            Long money = account.deposit(paymentDto.getMoney());
            TransactionHistory transactionHistory = new TransactionHistory(bankAccount, paymentDto.getDepositorContent(), LocalDateTime.now(), paymentDto.getMoney(), Type.DEPOSIT, money);
            transactionHistoryRepository.save(transactionHistory);
        }catch (Exception e){
            throw new BackAccountException.WithdrawException("입금 중 문제가 발생했습니다!");
        }
    }

    // 이미지
    public List<BankRequestDto> selectImage() {
        List<Bank> bankList = bankRepository.findAll();
        List<BankRequestDto> bankRequestDtos = new ArrayList<>();
        for(Bank bank  : bankList){
            bankRequestDtos.add(BankRequestDto.BankToBankRequestDto(bank));
        }
        return bankRequestDtos;
    }
}
