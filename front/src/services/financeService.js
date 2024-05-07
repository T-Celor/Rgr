import axios from 'axios';
import config from '@/config';

const baseUrl = config.backendUrl;

export async function getFinanceData(ticker, accumulating) {
  const urlObj = new URL(`${baseUrl}/finance/ticker/single`);
  urlObj.searchParams.set('ticker', ticker);
  urlObj.searchParams.set('accumulating', accumulating);
  const response = await axios.get(urlObj.toString());
  return response.data;
}

export async function getFinanceDataMultiple(tickers, accumulating) {
  const urlObj = new URL(`${baseUrl}/finance/ticker/multiple`);
  urlObj.searchParams.set('tickers', tickers);
  urlObj.searchParams.set('accumulating', accumulating);
  const response = await axios.get(urlObj.toString());
  return response.data;
}

export async function getWalletData(tickers, base, accumulating) {
  const urlObj = new URL(`${baseUrl}/wallet`);
  urlObj.searchParams.set('tickers', tickers);
  urlObj.searchParams.set('accumulating', accumulating);
  urlObj.searchParams.set('base', base);
  const response = await axios.get(urlObj.toString());
  return response.data;
}